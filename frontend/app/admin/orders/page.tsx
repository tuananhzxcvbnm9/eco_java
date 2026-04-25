export default function AdminOrdersPage() {
  return (
    <section>
      <h1>Admin - Quản lý đơn hàng</h1>
      <div className="panel">
        <table className="table">
          <thead>
            <tr><th>Order No</th><th>Customer</th><th>Total</th><th>Status</th></tr>
          </thead>
          <tbody>
            <tr><td>EC2026001</td><td>Nguyen Van A</td><td>₫2,290,000</td><td>PAID</td></tr>
            <tr><td>EC2026002</td><td>Tran Thi B</td><td>₫1,590,000</td><td>PENDING</td></tr>
          </tbody>
        </table>
      </div>
    </section>
  );
}
