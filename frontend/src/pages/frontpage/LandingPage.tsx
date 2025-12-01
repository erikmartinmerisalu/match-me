import React from 'react';
import { useNavigate } from 'react-router-dom';
import { useAuth } from '../../context/AuthContext';
import './landingpage.css';

const LandingPage: React.FC = () => {
  const navigate = useNavigate();
  const { loggedIn } = useAuth();

  // If user is logged in, redirect to home dashboard
  React.useEffect(() => {
    if (loggedIn) {
      navigate('/home');
    }
  }, [loggedIn, navigate]);

  const handleGetStarted = () => {
    navigate('/signup');
  };

  const handleJoinNow = () => {
    navigate('/signup');
  };

  const handleLogin = () => {
    navigate('/login');
  };

  return (
    <div className="landing-page">
      {/* Navigation */}
      <nav className="landing-nav">
        <div className="nav-container">
          {/* Logo */}
          <div className="logo">
            <div className="logo-shapes">
              <div className="shape-square"></div>
              <div className="shape-circle"></div>
              <div className="shape-triangle"></div>
            </div>
          </div>

          {/* Nav Links */}
          <div className="nav-links">
            <a href="#how-it-works">How it Works</a>
            <a href="#features">Features</a>
            <button onClick={handleLogin} className="nav-link-btn">Log In</button>
          </div>

          {/* CTA Button */}
          <button className="nav-cta-btn" onClick={handleJoinNow}>
            Join Now
          </button>
        </div>
      </nav>

      {/* Hero Section */}
      <section className="hero-section">
        <div className="hero-content">
          {/* Logo Icon */}
          <div className="hero-logo">
            <div className="logo-shape-large square"></div>
            <div className="logo-shape-large circle"></div>
            <div className="logo-shape-large triangle"></div>
          </div>

          {/* Headline */}
          <h1 className="hero-title">Gamely connects</h1>
          <h2 className="hero-subtitle">Gamers worldwide. Play together.</h2>

          {/* CTA Buttons */}
          <div className="hero-buttons">
            <button className="btn-primary" onClick={handleGetStarted}>
              Get Started
            </button>
            <a href="#features" className="btn-secondary">
              Explore Features
            </a>
          </div>
        </div>

        {/* Decorative Line */}
        <div className="decorative-line-container">
          <div className="decorative-line"></div>
        </div>
      </section>

      {/* How It Works Section */}
      <section id="how-it-works" className="how-it-works-section">
        <div className="section-container">
          <h2 className="section-title">How it Works</h2>
          
          <div className="steps-grid">
            <div className="step-card">
              <div className="step-number">1</div>
              <h3 className="step-title">Create Profile</h3>
              <p className="step-description">
                Set up your gaming profile with your favorite games, experience level, and preferences.
              </p>
            </div>

            <div className="step-card">
              <div className="step-number">2</div>
              <h3 className="step-title">Find Players</h3>
              <p className="step-description">
                Discover gamers nearby who share your interests and gaming style.
              </p>
            </div>

            <div className="step-card">
              <div className="step-number">3</div>
              <h3 className="step-title">Play Together</h3>
              <p className="step-description">
                Connect, chat, and team up with your new gaming friends.
              </p>
            </div>
          </div>
        </div>
      </section>

      {/* Features Section */}
      <section id="features" className="features-section">
        <div className="section-container">
          <h2 className="section-title">Features</h2>
          
          <div className="features-grid">
            <div className="feature-card">
              <div className="feature-icon">🎮</div>
              <h3 className="feature-title">Match by Games</h3>
              <p className="feature-description">
                Find players who love the same games you do. Filter by experience level, playstyle, and more.
              </p>
            </div>

            <div className="feature-card">
              <div className="feature-icon">💬</div>
              <h3 className="feature-title">Real-time Chat</h3>
              <p className="feature-description">
                Message your gaming buddies instantly with our built-in chat system.
              </p>
            </div>

            <div className="feature-card">
              <div className="feature-icon">📍</div>
              <h3 className="feature-title">Location-Based</h3>
              <p className="feature-description">
                Connect with gamers in your area for local tournaments or online sessions in your timezone.
              </p>
            </div>

            <div className="feature-card">
              <div className="feature-icon">🌍</div>
              <h3 className="feature-title">Global Community</h3>
              <p className="feature-description">
                Join a worldwide network of gamers. Make friends across borders and cultures.
              </p>
            </div>
          </div>
        </div>
      </section>

      {/* Sign Up Section */}
      <section id="signup" className="signup-section">
        <div className="signup-content">
          <h2 className="signup-title">Ready to start gaming?</h2>
          <p className="signup-subtitle">
            Join thousands of gamers connecting and playing together every day.
          </p>
          <button className="signup-btn" onClick={handleJoinNow}>
            Join Now - It's Free
          </button>
        </div>
      </section>
    </div>
  );
};

export default LandingPage;