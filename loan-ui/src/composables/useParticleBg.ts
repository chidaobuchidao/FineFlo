import { onMounted, onUnmounted, ref, type Ref } from 'vue'

interface Particle {
  x: number
  y: number
  vx: number
  vy: number
  radius: number
  opacity: number
}

export function useParticleBg(canvasRef: Ref<HTMLCanvasElement | null>) {
  let ctx: CanvasRenderingContext2D | null = null
  let particles: Particle[] = []
  let animFrame = 0
  let w = 0
  let h = 0

  function init() {
    const canvas = canvasRef.value
    if (!canvas) return
    ctx = canvas.getContext('2d')
    resize()
    particles = Array.from({ length: 80 }, () => ({
      x: Math.random() * w,
      y: Math.random() * h,
      vx: (Math.random() - 0.5) * 0.5,
      vy: (Math.random() - 0.5) * 0.5,
      radius: Math.random() * 2 + 1,
      opacity: Math.random() * 0.4 + 0.1,
    }))
    draw()
  }

  function resize() {
    const canvas = canvasRef.value
    if (!canvas) return
    w = canvas.width = window.innerWidth
    h = canvas.height = window.innerHeight
  }

  function draw() {
    if (!ctx || !canvasRef.value) return
    ctx.clearRect(0, 0, w, h)

    for (let i = 0; i < particles.length; i++) {
      const p = particles[i]
      p.x += p.vx
      p.y += p.vy

      if (p.x < 0 || p.x > w) p.vx *= -1
      if (p.y < 0 || p.y > h) p.vy *= -1

      ctx.beginPath()
      ctx.arc(p.x, p.y, p.radius, 0, Math.PI * 2)
      ctx.fillStyle = `rgba(99, 102, 241, ${p.opacity})`
      ctx.fill()
    }

    for (let i = 0; i < particles.length; i++) {
      for (let j = i + 1; j < particles.length; j++) {
        const dx = particles[i].x - particles[j].x
        const dy = particles[i].y - particles[j].y
        const dist = Math.sqrt(dx * dx + dy * dy)
        if (dist < 120) {
          ctx.beginPath()
          ctx.moveTo(particles[i].x, particles[i].y)
          ctx.lineTo(particles[j].x, particles[j].y)
          ctx.strokeStyle = `rgba(99, 102, 241, ${0.08 * (1 - dist / 120)})`
          ctx.lineWidth = 0.5
          ctx.stroke()
        }
      }
    }

    animFrame = requestAnimationFrame(draw)
  }

  onMounted(() => {
    init()
    window.addEventListener('resize', resize)
  })

  onUnmounted(() => {
    cancelAnimationFrame(animFrame)
    window.removeEventListener('resize', resize)
  })
}
