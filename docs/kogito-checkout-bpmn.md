# BPMN Checkout Marketplace (Kogito) + Saga Compensation

Dokumen ini menjelaskan model proses checkout marketplace menggunakan BPMN 2.0 di Kogito, termasuk pola Saga untuk kompensasi saat `inventory reserve` berhasil tetapi langkah lanjutan checkout gagal.

Catatan: pada kode saat ini, orkestrasi checkout sudah dijalankan oleh engine runtime Kogito melalui process `checkoutMarketplaceSaga`.

## Lokasi File BPMN

- `checkout-service/src/main/resources/checkout-marketplace-saga.bpmn2`

File ini bisa dibuka/import di Kogito VS Code extension atau Business Modeler untuk divisualisasikan.

## Service yang Digunakan dalam Proses

1. `Order Service`
- Mengirim event `order.checkout.requested` (trigger awal proses).

2. `Checkout Service` (service ini, Quarkus)
- Validasi payload + idempotency check (`processed_event`).
- Menjalankan orkestrasi checkout.
- Menulis `checkout`, `processed_event`, dan `outbox` secara transaksional.

3. `Inventory Service`
- Operasi utama: `reserve inventory`.
- Operasi kompensasi: `release inventory` (dipanggil saat saga rollback parsial).

4. `Messaging / Kafka`
- Input: `order.checkout.requested`
- Output sukses: `order.checkout.completed`
- Output gagal: `order.checkout.failed`

5. `Outbox Publisher Job`
- Membaca outbox `PENDING` lalu publish event ke Kafka.
- Menandai outbox sebagai `SENT`.

## Ilustrasi BPMN (Ringkas)

```text
Start (Checkout Requested)
  -> Validate + Idempotency
  -> [Checkout Saga Scope]
       -> Reserve Inventory
       -> Gateway: Reserved?
            - No  -> Publish checkout.failed (reserve failed) -> End Subprocess
            - Yes -> Process Checkout (DB + Outbox)
                    -> Gateway: Process Success?
                        - Yes -> Publish checkout.completed -> End Subprocess
                        - No  -> Error End (CHECKOUT_PROCESS_FAILED)

Boundary Error on Saga Scope (CHECKOUT_PROCESS_FAILED)
  -> Throw Compensation
  -> Release Inventory (compensation handler)
  -> Publish checkout.failed (compensated)
  -> End
```

## Skenario Kompensasi yang Ditangani

Kasus yang diminta:
- `reserve inventory` sudah sukses,
- lalu `process checkout` gagal (misalnya gagal persist, validasi bisnis lanjutan, atau gagal di langkah orkestrasi setelah reserve).

Maka proses:
1. Menangkap error pada boundary event.
2. Menjalankan compensation throw event.
3. Menjalankan task kompensasi `Release Inventory`.
4. Mempublikasikan `order.checkout.failed` dengan alasan kompensasi.

## Mapping ke Implementasi Saat Ini

1. `Task_ValidateRequest`
- Mapping ke `CheckoutRequestedConsumer` + `CheckoutSagaWorkflowService.validateRequestAndIdempotency(...)`.

2. `Task_ReserveInventory`
- Mapping ke `InventoryGateway.reserve(...)`.

3. `Task_ProcessCheckout (DB + Outbox)`
- Mapping ke `CheckoutSagaWorkflowService.processCheckout(...)` (persist checkout).

4. `Task_PublishCheckoutCompleted` / `Task_PublishCheckoutFailed_*`
- Mapping ke `CheckoutSagaWorkflowService.publish*` yang menulis event ke outbox (`PENDING`), lalu dipublish oleh `OutboxPublisherJob`.

5. `Task_ReleaseInventory (Compensation)`
- Sudah tersedia hook kompensasi di `InventoryGateway.release(...)`.
- Implementasi saat ini memakai `MockInventoryGateway` (return `true`), untuk produksi perlu dihubungkan ke endpoint inventory release yang nyata.
