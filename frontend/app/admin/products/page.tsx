import { products, currency } from '@/lib/mock-data';

export default function AdminProductsPage() {
  return (
    <section>
      <h1>Admin - Quản lý sản phẩm</h1>
      <div className="panel">
        <table className="table">
          <thead>
            <tr><th>Tên</th><th>Brand</th><th>Giá</th><th>Trạng thái</th></tr>
          </thead>
          <tbody>
            {products.map((p) => (
              <tr key={p.id}>
                <td>{p.name}</td>
                <td>{p.brand}</td>
                <td>{currency(p.price)}</td>
                <td>ACTIVE</td>
              </tr>
            ))}
          </tbody>
        </table>
      </div>
    </section>
  );
}
