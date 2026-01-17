# 第一阶段：构建 Vue (改用了 node:22-alpine)
FROM node:22-alpine AS builder
WORKDIR /app
COPY package*.json ./
RUN npm install
COPY . .
RUN npm run build

# 第二阶段：Nginx 部署
FROM nginx:alpine
# 复制构建好的 dist 文件夹到 Nginx 目录
COPY --from=builder /app/dist /usr/share/nginx/html
# 复制我们自定义的 nginx 配置
COPY nginx.conf /etc/nginx/conf.d/default.conf
EXPOSE 80
CMD ["nginx", "-g", "daemon off;"]