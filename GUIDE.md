# 📘 Hướng Dẫn Vận Hành Đồ Án Monolith (Professional Baseline)

Hệ thống này được thiết kế theo tiêu chuẩn DevOps hiện đại, tập trung vào tính ổn định, bảo mật và khả năng giám sát.

## 🏗 1. Cấu trúc hệ thống
- **Backend**: Spring Boot (Java 21).
- **Frontend**: React (TypeScript).
- **Database**: PostgreSQL.
- **CI/CD**: GitHub Actions + SonarQube + Trivy.
- **Deployment**: ArgoCD + Argo Rollouts (Blue-Green Strategy).

## 🛠 2. Cách chạy thử (Local)
1. Cài đặt Docker & Docker Compose.
2. Tại thư mục gốc, chạy:
   ```bash
   docker-compose up --build
   ```
3. Truy cập:
   - Frontend: http://localhost:3000
   - Backend API: http://localhost:8081
   - Database: localhost:5433 (postgres/postgres)

## 🚀 3. Quy trình triển khai (Production - GitOps)
1. **Push code**: Khi bạn push lên GitHub, pipeline sẽ build, quét bảo mật (Trivy), quét chất lượng (SonarQube).
2. **Blue-Green**: Sử dụng Argo Rollouts để deploy bản mới song song bản cũ, tráo đổi traffic khi bản mới ổn định.
3. **Monitoring**: Prometheus (Port 30090) & Grafana (Port 30300) để theo dõi hệ thống.
4. **Slack**: Nhận báo cáo tức thì về tình trạng deploy.

## 🔑 4. Cấu hình Secrets
Nhớ thêm đầy đủ các biến vào GitHub Secrets: `DOCKER_USERNAME`, `DOCKER_PASSWORD`, `SONAR_TOKEN`, `SONAR_HOST_URL`, `SLACK_WEBHOOK_URL`, `JWT_SECRET`.
