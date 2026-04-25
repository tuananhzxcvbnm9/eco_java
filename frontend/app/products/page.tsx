import { ProductCard } from '@/components/ProductCard';
import { products } from '@/lib/mock-data';

export const metadata = {
  title: 'Danh sách sản phẩm | EcoCommerce',
  description: 'Filter theo category/brand/price/rating và sort theo latest/price/best-selling.'
};

export default function ProductListingPage() {
  return (
    <section>
      <h1>Danh sách sản phẩm</h1>
      <div className="panel">
        <p className="muted">(Mock) Bộ lọc: Danh mục, giá, thương hiệu, màu, size, đánh giá + sort.</p>
      </div>
      <br />
      <div className="grid">
        {products.map((product) => (
          <ProductCard key={product.id} product={product} />
        ))}
      </div>
    </section>
  );
}
