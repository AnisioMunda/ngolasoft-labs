# ==============================================================
# Development
# ==============================================================
FROM node:20-alpine AS development

WORKDIR /app

# Copiar apenas o package.json primeiro para cache do Docker.
# O package-lock.json será gerado automaticamente pelo npm install.
COPY package.json ./

# "npm install" gera o package-lock.json automaticamente
# se ele não existir — mais flexível que "npm ci"
RUN npm install

COPY . .

EXPOSE 4200

CMD ["npx", "ng", "serve", \
     "--host", "0.0.0.0", \
     "--port", "4200", \
     "--poll", "2000"]


# ==============================================================
# Builder
# ==============================================================
FROM node:20-alpine AS builder

WORKDIR /app

COPY package.json ./
RUN npm install

COPY . .

RUN npx ng build \
    --configuration production \
    --output-path /dist


# ==============================================================
# Production
# ==============================================================
FROM nginx:alpine AS production

RUN rm /etc/nginx/conf.d/default.conf

COPY nginx.conf /etc/nginx/conf.d/default.conf

COPY --from=builder /dist/browser /usr/share/nginx/html

EXPOSE 80

CMD ["nginx", "-g", "daemon off;"]