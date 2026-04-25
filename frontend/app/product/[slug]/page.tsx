import Image from 'next/image';
import { notFound } from 'next/navigation';
import { products, currency } from '@/lib/mock-data';

export function generateMetadata({ params }: { params: { slug: string } }) {
  const product = products.find((item) => item.slug === params.slug);
  if (!product) return { title: 'Không tìm thấy sản phẩm' };
  return {
    title: `${product.name} | EcoCommerce`,
    description: `Mua ${product.name} chính hãng - ${currency(product.price)}`,
    openGraph: {
      title: product.name,
      description: `${product.brand} - ${currency(product.price)}`,
      images: [product.image]
    }
  };
}

export default function ProductDetailPage({ params }: { params: { slug: string } }) {
  const product = products.find((item) => item.slug === params.slug);
  if (!product) return notFound();

  const jsonLd = {
    '@context': 'https://schema.org',
    '@type': 'Product',
    name: product.name,
    image: product.image,
    brand: product.brand,
    offers: { '@type': 'Offer', priceCurrency: 'VND', price: product.price, availability: 'https://schema.org/InStock' },
    aggregateRating: { '@type': 'AggregateRating', ratingValue: product.rating, reviewCount: 10 }
  };

  return (
    <article className="panel">
      <div className="row" style={{ alignItems: 'start' }}>
        <div style={{ position: 'relative', width: '100%', maxWidth: 500, aspectRatio: '4 / 3' }}>
          <Image src={product.image} alt={product.name} fill sizes="100vw" />
        </div>
        <div>
          <h1>{product.name}</h1>
          <p className="muted">Brand: {product.brand}</p>
          <p>Giá: <strong>{currency(product.price)}</strong></p>
          <p>Tồn kho: Còn hàng</p>
          <button className="btn">Thêm vào giỏ</button>
        </div>
      </div>
      <script type="application/ld+json" dangerouslySetInnerHTML={{ __html: JSON.stringify(jsonLd) }} />
    </article>
  );
}
