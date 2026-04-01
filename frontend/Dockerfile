# Plane Lite Frontend Dockerfile
# Multi-stage build for Vue application

# Stage 1: Build
FROM node:18-alpine AS builder

WORKDIR /app

# Copy package.json and install dependencies (cache layer)
COPY package*.json ./
RUN npm ci

# Copy source code
COPY . .

# Build application
RUN npm run build

# Stage 2: Runtime
FROM nginx:alpine

# Copy built artifact
COPY --from=builder /app/dist /usr/share/nginx/html

# Copy nginx configuration
COPY nginx.conf /etc/nginx/conf.d/default.conf

# Expose port
EXPOSE 80

# Health check
HEALTHCHECK --interval=30s --timeout=10s --start-period=10s --retries=3 \
  CMD curl -f http://localhost/ || exit 1

# Start nginx
CMD ["nginx", "-g", "daemon off;"]
