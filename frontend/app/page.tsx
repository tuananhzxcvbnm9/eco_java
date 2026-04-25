import { ProductCard } from '@/components/ProductCard';
import { products } from '@/lib/mock-data';

export default function HomePage() {
  const featured = products.filter((p) => p.isFeatured);

  return (
    <section>
      <h1>Modern E-commerce Storefront</h1>
      <p className="muted">SEO-ready với Next.js SSR/ISR, responsive và component tái sử dụng.</p>
      <h2>Sản phẩm nổi bật</h2>
      <div className="grid">
        {featured.map((product) => (
          <ProductCard key={product.id} product={product} />
        ))}
      </div>
    </section>
  );
}
