/** @type {import('tailwindcss').Config} */
module.exports = {
  content: [
    "./src/**/*.{js,jsx,ts,tsx}",
  ],
  theme: {
    extend: {
      fontFamily: {
        sans: ['Inter', 'ui-sans-serif', 'system-ui', '-apple-system', 'BlinkMacSystemFont', 'Segoe UI', 'Roboto', 'Helvetica Neue', 'Arial', 'Noto Sans', 'sans-serif'],
      },
      colors: {
        gray: {
          50: '#f9fafb',
          100: '#f3f4f6',
          200: '#e5e7eb',
          300: '#d1d5db',
          400: '#9ca3af',
          500: '#6b7280',
          600: '#4b5563',
          700: '#374151',
          800: '#1f2937',
          900: '#111827',
        },
        blue: {
          50: '#eff6ff',
          100: '#dbeafe',
          200: '#bfdbfe',
          300: '#93c5fd',
          400: '#60a5fa',
          500: '#3b82f6',
          600: '#2563eb',
          700: '#1d4ed8',
          800: '#1e40af',
          900: '#1e3a8a',
        },
      },
      typography: {
        DEFAULT: {
          css: {
            maxWidth: 'none',
            color: '#374151',
            lineHeight: '1.75',
            p: {
              marginBottom: '1rem',
            },
            h1: {
              fontSize: '2.25rem',
              lineHeight: '2.5rem',
              fontWeight: '700',
              marginTop: '2rem',
              marginBottom: '1rem',
            },
            h2: {
              fontSize: '1.875rem',
              lineHeight: '2.25rem',
              fontWeight: '700',
              marginTop: '2rem',
              marginBottom: '1rem',
            },
            h3: {
              fontSize: '1.5rem',
              lineHeight: '2rem',
              fontWeight: '700',
              marginTop: '2rem',
              marginBottom: '1rem',
            },
            strong: {
              fontWeight: '700',
            },
            em: {
              fontStyle: 'italic',
            },
            'ul, ol': {
              paddingLeft: '1.5rem',
              marginBottom: '1rem',
            },
            li: {
              marginBottom: '0.5rem',
            },
            blockquote: {
              borderLeft: '4px solid #e5e7eb',
              paddingLeft: '1rem',
              fontStyle: 'italic',
              margin: '1.5rem 0',
              color: '#6b7280',
            },
            a: {
              color: '#2563eb',
              textDecoration: 'underline',
              '&:hover': {
                color: '#1d4ed8',
              },
            },
          },
        },
      },
    },
  },
  plugins: [
    require('@tailwindcss/forms'),
  ],
}