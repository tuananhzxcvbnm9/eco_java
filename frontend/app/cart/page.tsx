import { currency, products } from '@/lib/mock-data';

export default function CartPage() {
  const items = products.slice(0, 2);
  const subtotal = items.reduce((sum, item) => sum + item.price, 0);

  return (
    <section>
      <h1>Giỏ hàng</h1>
      <div className="panel">
        {items.map((item) => (
          <div key={item.id} className="row" style={{ padding: '.5rem 0' }}>
            <span>{item.name}</span>
            <strong>{currency(item.price)}</strong>
          </div>
        ))}
        <hr />
        <div className="row">
          <span>Tạm tính</span>
          <strong>{currency(subtotal)}</strong>
        </div>
      </div>
    </section>
  );
}
