# 📘 Cẩm Nang Vận Hành Hệ Thống End-to-End Monolith

Chào mừng bạn đến với hệ thống được tự động hóa hoàn toàn. Tài liệu này tóm tắt mọi thứ bạn cần biết để vận hành dự án.

## 🏗 1. Cấu trúc Dự án (Monolith Architecture)
Dự án được gộp lại thành một khối thống nhất nhưng vẫn đảm bảo đầy đủ chức năng:
- `/ecommerce-platform/backend`: Spring Boot (Java 21) - Trung tâm xử lý.
- `/ecommerce-platform/frontend`: React (TypeScript) - Giao diện người dùng.
- `/k8s`: Manifests triển khai lên Kubernetes (Postgres, Blue-Green Rollout, Monitoring).
- `/.github/workflows`: Pipeline CI/CD "tất cả trong một".
- `/argocd`: Cấu hình GitOps.

## 🛠 2. Chạy thử dưới Local (Pre-deployment Test)
Trước khi push lên GitHub, hãy đảm bảo hệ thống chạy ổn định bằng Docker Compose:
```bash
# Tại thư mục gốc dự án
docker-compose up --build
```
- **Frontend**: http://localhost:3000
- **Backend**: http://localhost:8080
- **DB (Postgres)**: localhost:5432 (User/Pass: postgres/postgres)

## 🚀 3. Quy trình CI/CD (GitHub Actions)
Khi bạn `git push` lên nhánh `main`, quy trình sau sẽ tự động kích hoạt:
1. **SonarQube Analysis**: Quét chất lượng mã nguồn Backend.
2. **Docker Build**: Build images cho cả Backend và Frontend.
3. **Trivy Security Scan**: Quét lỗ hổng bảo mật trong Image. Nếu có lỗi nghiêm trọng -> **Dừng ngay lập tức**.
4. **Push Images**: Đẩy images an toàn lên Docker Hub với tag SHA.
5. **GitOps Update**: Tự động sửa file trong thư mục `k8s/` để trỏ vào image mới.
6. **Slack Notification**: Báo cáo kết quả về kênh Slack của bạn.

## 🎡 4. Triển khai lên K8s (ArgoCD & Blue-Green)
Hệ thống sử dụng chiến lược **Blue-Green Deployment**:
- **Trạng thái**: Bản cũ (Blue) đang chạy.
- **Khi có bản mới**: Bản mới (Green) sẽ được tạo ra song song.
- **Chuyển đổi**: Sau khi Green sẵn sàng, traffic được tráo đổi 100% sang Green.
- **Lợi ích**: Không có thời gian chết (Zero Downtime) và cực kỳ an toàn.

## 📊 5. Giám sát hệ thống (Monitoring)
Sau khi deploy thành công, bạn có thể truy cập các công cụ giám sát:
- **Prometheus**: http://[Node-IP]:30090 (Thu thập chỉ số CPU/RAM/Request).
- **Grafana**: http://[Node-IP]:30300 (Giao diện biểu đồ giám sát cực đẹp).

## 🔑 6. Danh sách GitHub Secrets cần thiết
Hãy đảm bảo bạn đã thêm các biến này vào **GitHub -> Settings -> Secrets**:
1. `DOCKER_USERNAME`: Tên Docker Hub.
2. `DOCKER_PASSWORD`: Password/Token Docker Hub.
3. `SONAR_TOKEN`: Token từ SonarQube.
4. `SONAR_HOST_URL`: Địa chỉ server SonarQube.
5. `SLACK_WEBHOOK_URL`: Link Webhook từ Slack.
6. `JWT_SECRET`: Chuỗi bí mật cho JWT Backend.

---
**Chúc bạn có những trải nghiệm DevOps tuyệt vời với dự án này!** 🚀
