import Link from "next/link";

export default function Home() {
  return (
    <div className="page">
      {/* 1. HERO SECTION */}
      <section className="text-center py-12 mb-8">
        <h1 className="text-5xl font-bold mb-4" style={{ color: 'var(--foreground)' }}>
          Connecting surplus food with people who can actually use it.
        </h1>
        <p className="text-xl text-muted mb-8 max-w-2xl mx-auto">
          Rescue unsold food bundles from your favorite local cafeterias and restaurants at a discount.
        </p>
        <div className="flex justify-center gap-4">
          <Link href="/bundles" className="btn btn-primary">
            Find Food Now
          </Link>
          <Link href="/register" className="btn btn-secondary">
            Join as Seller
          </Link>
        </div>
      </section>

      {/* 2. HOW IT WORKS  */}
      <section className="mb-16">
        <div className="text-center mb-8">
          <h2 className="text-3xl font-bold">How It Works</h2>
          <p className="text-muted">Three simple steps to fight food waste.</p>
        </div>
        
        <div className="grid grid-3">
          
          <div className="card text-center">
            <div className="text-4xl mb-4">🔍</div>
            <h3 className="text-xl font-bold mb-2">Browse</h3>
            <p className="text-muted">Discover surprise food bundles near you that are too good to waste.</p>
          </div>

          <div className="card text-center">
            <div className="text-4xl mb-4">📅</div>
            <h3 className="text-xl font-bold mb-2">Reserve</h3>
            <p className="text-muted">Secure your bundle instantly through the app for a fraction of the price.</p>
          </div>

          <div className="card text-center">
            <div className="text-4xl mb-4">🛍️</div>
            <h3 className="text-xl font-bold mb-2">Pick Up</h3>
            <p className="text-muted">Show your code at the store, pick up your bag, and enjoy!</p>
          </div>
        </div>
      </section>

      {/* 3. LIVE STATS (We need to integrate with analytics)  */}
      <section className="mb-16">
        <div className="card bg-green-50 border-green-200">
          <div className="grid grid-3 text-center">
            <div>
              <div className="text-3xl font-bold text-green-600">placeholder</div>
              <div className="text-muted">Food Saved</div>
            </div>
            <div>
              <div className="text-3xl font-bold text-green-600">placeholder</div>
              <div className="text-muted">CO2 Prevented</div>
            </div>
            <div>
              <div className="text-3xl font-bold text-green-600">placeholder</div>
              <div className="text-muted">Money Saved</div>
            </div>
          </div>
        </div>
      </section>

      {/* 4. GAMIFICATION  */}
      <section className="text-center mb-16">
        <h2 className="text-3xl font-bold mb-4">Show Off When You Save</h2>
        <p className="text-muted mb-6">Earn badges and maintain streaks for every bundle you rescue.</p>
        
        <div className="grid grid-3 max-w-4xl mx-auto">
           <div className="badge badge-warning text-lg py-2 px-4 justify-center">7 Day Streak</div>
           <div className="badge badge-primary text-lg py-2 px-4 justify-center">Zero Waste</div>
           <div className="badge badge-warning text-lg py-2 px-4 justify-center">Top Rescuer</div>
        </div>
      </section>

      {/* 5. CALL TO ACTION */}
      <section className="text-center py-12 bg-green-500 rounded-2xl">
        <h2 className="text-3xl font-bold mb-4">Start saving now!</h2>
        <Link href="/register" className="btn btn-primary">
          Create Free Account
        </Link>
      </section>
    </div>
  );
}
