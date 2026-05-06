# V-Shop: Ecommerce Monolithic Platform

Một ứng dụng thương mại điện tử hoàn chỉnh (Full-stack) được thiết kế theo kiến trúc Monolithic, tích hợp quy trình DevSecOps hiện đại với chiến lược triển khai Blue-Green Deployment.

---

## 🏗 Kiến trúc hệ thống

[Dán ảnh sơ đồ kiến trúc của bạn vào đây]

Hệ thống được thiết kế tối giản nhưng vẫn đảm bảo đầy đủ các thành phần của một ứng dụng thực tế, bao gồm:
- **Client Tier**: Giao diện React (TypeScript) tương tác với API qua Axios.
- **Application Tier**: Spring Boot xử lý logic nghiệp vụ, bảo mật với JWT.
- **Data Tier**: PostgreSQL lưu trữ dữ liệu sản phẩm, người dùng và đơn hàng.
- **Infrastructure**: Triển khai trên Kubernetes (K8s) thông qua GitOps (ArgoCD).

---

## 🛠 Tech Stack

### Backend
- **Core**: Java 21, Spring Boot 4.x
- **Security**: Spring Security, JWT (Stateless Authentication)
- **Database**: Spring Data JPA, PostgreSQL
- **Validator**: Hibernate Validator

### Frontend
- **Framework**: React 18, TypeScript
- **Styling**: Vanilla CSS (Custom UI Components)
- **State Management**: React Context API

### DevOps & Infrastructure
- **CI/CD**: GitHub Actions
- **Containerization**: Docker, Docker Compose
- **Orchestration**: Kubernetes
- **GitOps**: ArgoCD
- **Deployment Strategy**: Argo Rollouts (Blue-Green Deployment)

---

## 🚀 Chức năng chính

- **Quản lý người dùng**: Đăng ký, đăng nhập và phân quyền (User/Admin).
- **Cửa hàng**: Hiển thị danh sách sản phẩm, chi tiết sản phẩm và danh mục.
- **Giỏ hàng**: Thêm/sửa/xóa sản phẩm trong giỏ hàng theo từng người dùng.
- **Đặt hàng**: Quy trình đặt hàng nhanh chóng và lưu vết lịch sử đơn hàng.
- **Quản trị (Admin)**: Quản lý danh mục và người dùng hệ thống.

---

## 📦 Hướng dẫn cài đặt nhanh

### Yêu cầu hệ thống
- Docker & Docker Compose
- JDK 21 (nếu chạy local không qua Docker)
- Node.js (nếu chạy local không qua Docker)

### Chạy bằng Docker Compose
```bash
# Clone dự án
git clone https://github.com/davidmoi2135/End-to-End-Monolithic-Deployment.git

# Khởi chạy toàn bộ hệ thống
docker-compose up --build
```
- **Frontend**: http://localhost:3000
- **Backend**: http://localhost:8081

---

## 🔄 Quy trình Triển khai (CI/CD)

Dự án áp dụng quy trình tự động hóa hoàn toàn từ khâu kiểm thử đến triển khai:
1. **Build & Scan**: GitHub Actions tự động build Docker Image và quét lỗ hổng bảo mật bằng Trivy.
2. **Push Image**: Đẩy Image lên Docker Hub với Tag tương ứng.
3. **GitOps Trigger**: Pipeline cập nhật Manifest YAML trong repository.
4. **ArgoCD Sync**: ArgoCD tự động đồng bộ trạng thái mới nhất lên Kubernetes Cluster.
5. **Blue-Green Rollout**: Sử dụng Argo Rollouts để chuyển đổi phiên bản không gây gián đoạn (Zero Downtime).

---

## 📞 Liên hệ
- **Tác giả**: [Tên của bạn]
- **Email**: [Email của bạn]
- **LinkedIn**: [Link LinkedIn của bạn]
