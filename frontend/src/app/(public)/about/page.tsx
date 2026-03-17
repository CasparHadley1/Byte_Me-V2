"use client";

import Link from "next/link";

export default function AboutPage() {
  return (
    <div className="page" style={{ maxWidth: "48rem", margin: "0 auto" }}>
      <div className="mb-6">
        <h1 className="page-title">About Byte Me</h1>
        <p className="page-subtitle">Fighting food waste, one bundle at a time.</p>
      </div>

      <div className="card mb-6" style={{ padding: "2rem" }}>
        <h2 className="text-xl font-semibold mb-4">What We Do</h2>
        <p style={{ lineHeight: 1.7 }}>
          Byte Me is a platform that connects food sellers with charitable organisations to
          rescue surplus food. Sellers list bundles of food that would otherwise go to waste,
          and organisations reserve and collect them at discounted prices.
        </p>
        <p style={{ lineHeight: 1.7, marginTop: "1rem" }}>
          Our demand forecasting system helps sellers post the right quantities, while
          gamification features like streaks and badges keep organisations engaged with
          regular collections.
        </p>
      </div>

      <div className="card mb-6" style={{ padding: "2rem" }}>
        <h2 className="text-xl font-semibold mb-4">How It Works</h2>
        <div style={{ display: "flex", flexDirection: "column", gap: "1rem" }}>
          <div><strong>For Sellers:</strong> List your surplus food as bundles, set pricing and pickup windows, and use forecasting insights to reduce over-production.</div>
          <div><strong>For Organisations:</strong> Browse available bundles, reserve what you need, and collect using your claim code during the pickup window.</div>
        </div>
      </div>

      <div className="card mb-6" style={{ padding: "2rem" }}>
        <h2 className="text-xl font-semibold mb-4">Why It Matters</h2>
        <p style={{ lineHeight: 1.7 }}>
          The UK wastes around 9.5 million tonnes of food annually. Each kilogram of food
          diverted from landfill avoids approximately 2.5 kg of CO2 equivalent emissions
          (WRAP UK). By making surplus food accessible, we help reduce waste, cut emissions,
          and support communities.
        </p>
      </div>

      <div className="text-center">
        <Link href="/register" className="btn btn-primary">Get Started</Link>
      </div>
    </div>
  );
}
