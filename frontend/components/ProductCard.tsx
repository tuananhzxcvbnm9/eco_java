import Image from 'next/image';
import Link from 'next/link';
import { Product, currency } from '@/lib/mock-data';

export function ProductCard({ product }: { product: Product }) {
  return (
    <article className="card">
      <div className="media">
        <Image src={product.image} alt={product.name} fill sizes="(max-width: 768px) 100vw, 33vw" />
      </div>
      <div className="content">
        <p className="badge">{product.brand}</p>
        <h3>
          <Link href={`/product/${product.slug}`}>{product.name}</Link>
        </h3>
        <p className="muted">Rating {product.rating} / 5</p>
        <div className="row">
          <strong>{currency(product.price)}</strong>
          <button className="btn">Add to cart</button>
        </div>
      </div>
    </article>
  );
}
