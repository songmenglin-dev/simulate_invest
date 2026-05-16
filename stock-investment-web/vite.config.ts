import { defineConfig } from 'vite'
import vue from '@vitejs/plugin-vue'
import path from 'path'

// https://vite.dev/config/
export default defineConfig({
  plugins: [vue()],
  resolve: {
    alias: {
      '@': path.resolve(__dirname, './src'),
    },
  },
  server: {
    proxy: {
      '/api/user': {
        target: 'http://localhost:8081',
        changeOrigin: true,
        rewrite: (p) => p.replace(/^\/api\/user/, '/user'),
      },
      '/api/market': {
        target: 'http://127.0.0.1:8083',
        changeOrigin: true,
        rewrite: (p) => p.replace(/^\/api\/market/, '/market'),
      },
      '/api/portfolio': {
        target: 'http://localhost:8084',
        changeOrigin: true,
        rewrite: (p) => p.replace(/^\/api\/portfolio/, '/portfolio'),
      },
      '/api/order': {
        target: 'http://localhost:8082',
        changeOrigin: true,
        rewrite: (p) => p.replace(/^\/api\/order/, '/order'),
      },
      '/api/analysis': {
        target: 'http://localhost:8085',
        changeOrigin: true,
        rewrite: (p) => p.replace(/^\/api\/analysis/, '/analysis'),
      },
      '/api/conditional-order': {
        target: 'http://localhost:8082',
        changeOrigin: true,
        rewrite: (p) => p.replace(/^\/api\/conditional-order/, '/conditional-order'),
      },
      '/api/watchlist': {
        target: 'http://127.0.0.1:8083',
        changeOrigin: true,
        rewrite: (p) => p.replace(/^\/api\/watchlist/, '/watchlist'),
      },
      '/api/alert': {
        target: 'http://127.0.0.1:8083',
        changeOrigin: true,
        rewrite: (p) => p.replace(/^\/api\/alert/, '/alert'),
      },
      '/api/backtest': {
        target: 'http://localhost:8085',
        changeOrigin: true,
        rewrite: (p) => p.replace(/^\/api\/backtest/, '/backtest'),
      },
      '/avatars': {
        target: 'http://192.168.100.145:9000',
        changeOrigin: true,
      },
    },
  },
})