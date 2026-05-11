/**
 * Canvas Particle System — for Login/Register pages
 * 60fps, responsive, Indigo/Cyan particles on dark background
 */

class ParticleSystem {
  constructor(canvasId) {
    this.canvas = document.getElementById(canvasId);
    if (!this.canvas) return;

    this.ctx = this.canvas.getContext('2d');
    this.particles = [];
    this.mouseX = -1000;
    this.mouseY = -1000;
    this.animationId = null;

    this.colors = ['#6366F1', '#818CF8', '#06B6D4', '#22D3EE', '#A78BFA'];

    this.resize();
    this.init(80);
    this.bindEvents();
    this.animate();
  }

  resize() {
    this.width = window.innerWidth;
    this.height = window.innerHeight;
    this.canvas.width = this.width;
    this.canvas.height = this.height;
  }

  init(count) {
    this.particles = [];
    for (let i = 0; i < count; i++) {
      this.particles.push({
        x: Math.random() * this.width,
        y: Math.random() * this.height,
        vx: (Math.random() - 0.5) * 0.4,
        vy: (Math.random() - 0.5) * 0.4,
        radius: Math.random() * 2 + 1,
        color: this.colors[Math.floor(Math.random() * this.colors.length)],
        opacity: Math.random() * 0.5 + 0.2,
      });
    }
  }

  bindEvents() {
    window.addEventListener('resize', () => this.resize());
    document.addEventListener('mousemove', (e) => {
      this.mouseX = e.clientX;
      this.mouseY = e.clientY;
    });
  }

  drawParticle(p) {
    this.ctx.beginPath();
    this.ctx.arc(p.x, p.y, p.radius, 0, Math.PI * 2);
    this.ctx.fillStyle = p.color;
    this.ctx.globalAlpha = p.opacity;
    this.ctx.fill();
  }

  drawConnection(a, b) {
    const dist = Math.hypot(a.x - b.x, a.y - b.y);
    if (dist < 120) {
      const opacity = (1 - dist / 120) * 0.15;
      this.ctx.beginPath();
      this.ctx.moveTo(a.x, a.y);
      this.ctx.lineTo(b.x, b.y);
      this.ctx.strokeStyle = '#6366F1';
      this.ctx.globalAlpha = opacity;
      this.ctx.lineWidth = 0.5;
      this.ctx.stroke();
    }
  }

  animate() {
    this.ctx.clearRect(0, 0, this.width, this.height);

    for (let i = 0; i < this.particles.length; i++) {
      const p = this.particles[i];

      p.x += p.vx;
      p.y += p.vy;

      if (p.x < 0) p.x = this.width;
      if (p.x > this.width) p.x = 0;
      if (p.y < 0) p.y = this.height;
      if (p.y > this.height) p.y = 0;

      // Mouse attraction
      const dx = this.mouseX - p.x;
      const dy = this.mouseY - p.y;
      const dist = Math.hypot(dx, dy);
      if (dist < 200) {
        p.vx += (dx / dist) * 0.02;
        p.vy += (dy / dist) * 0.02;
      }

      // Damping
      p.vx *= 0.999;
      p.vy *= 0.999;

      // Speed limit
      const speed = Math.hypot(p.vx, p.vy);
      if (speed > 1) {
        p.vx = (p.vx / speed) * 1;
        p.vy = (p.vy / speed) * 1;
      }

      this.drawParticle(p);

      for (let j = i + 1; j < this.particles.length; j++) {
        this.drawConnection(p, this.particles[j]);
      }
    }

    this.ctx.globalAlpha = 1;
    this.animationId = requestAnimationFrame(() => this.animate());
  }

  destroy() {
    if (this.animationId) {
      cancelAnimationFrame(this.animationId);
    }
  }
}
