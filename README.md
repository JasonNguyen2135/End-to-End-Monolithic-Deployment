# Ecommerce Monolith Application

Dự án này là một ứng dụng thương mại điện tử hoàn chỉnh được xây dựng theo kiến trúc **Monolith** (Baseline) để dễ dàng quản lý và triển khai.

## 🚀 Công nghệ sử dụng
- **Backend**: Java 21, Spring Boot 4.0, Spring Security (JWT), Spring Data JPA.
- **Frontend**: React, TypeScript, Axios.
- **Database**: PostgreSQL.
- **DevOps**: Docker, GitHub Actions, ArgoCD, Argo Rollouts (Blue-Green Deployment).

## 📂 Cấu trúc dự án
- `ecommerce-platform/backend`: Mã nguồn xử lý logic phía máy chủ.
- `ecommerce-platform/frontend`: Mã nguồn giao diện người dùng.
- `.github/workflows/`: Quy trình CI/CD tự động.
- `k8s/`: Các file cấu hình triển khai lên Kubernetes (Postgres, Rollout, Deployment).
- `argocd/`: Cấu hình GitOps để đồng bộ với ArgoCD.

## 🛠 Cách chạy dự án dưới máy Local (Docker Compose)
1. Đảm bảo bạn đã cài đặt Docker và Docker Compose.
2. Chạy lệnh:
   ```bash
   docker-compose up --build
   ```
3. Truy cập:
   - Frontend: `http://localhost:3000`
   - Backend API: `http://localhost:8080`

## 🔄 Quy trình CI/CD & Blue-Green Deployment
1. **CI**: Khi push code lên nhánh `main`, GitHub Actions sẽ build Docker images cho cả Frontend và Backend.
2. **Security Scan**: Quét lỗ hổng bảo mật bằng Trivy.
3. **GitOps**: Pipeline tự động cập nhật tag image mới vào thư mục `k8s/`.
4. **CD**: ArgoCD nhận diện thay đổi và thực hiện chiến lược **Blue-Green Deployment** trên Kubernetes:
   - Bản mới (Green) được deploy song song bản cũ (Blue).
   - Sau khi sẵn sàng, traffic được chuyển 100% sang bản mới.

## 🔑 Cấu hình GitHub Secrets cần thiết
Vui lòng thiết lập các biến sau trong GitHub Settings:
- `DOCKER_USERNAME`, `DOCKER_PASSWORD`
- `SONAR_TOKEN`, `SONAR_HOST_URL`
- `SLACK_WEBHOOK_URL`
- `JWT_SECRET`
