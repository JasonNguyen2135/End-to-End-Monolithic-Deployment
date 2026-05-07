# ECOMMERCE MONOLITHIC DEPLOYMENT PIPELINE

Hệ thống thương mại điện tử Full-stack được thiết kế để minh họa quy trình triển khai DevSecOps hiện đại trên nền tảng Kubernetes.

---

## KIẾN TRÚC HỆ THỐNG

<img width="986" height="515" alt="image" src="https://github.com/user-attachments/assets/a31533b5-7967-4097-a18b-12daecded966" />

Dự án tập trung vào việc tự động hóa chu kỳ sống phát triển phần mềm, bao gồm các thành phần:
- Giao diện người dùng: React (TypeScript)
- Logic nghiệp vụ: Spring Boot (Java 21)
- Lưu trữ dữ liệu: PostgreSQL
- Hạ tầng triển khai: Kubernetes Cluster

---

## CÔNG NGHỆ SỬ DỤNG

BACKEND
- Java 21 và Spring Boot 4.x
- Spring Security với cơ chế xác thực JWT
- Spring Data JPA kết nối PostgreSQL

FRONTEND
- React 18 và TypeScript
- Axios giao tiếp API

DEVOPS VÀ HẠ TẦNG
- Infrastructure as Code: Terraform
- Configuration Management: Ansible (Kubespray)
- CI/CD Pipeline: GitHub Actions
- Quản lý container: Docker
- Điều phối container: Kubernetes Cluster (tự vận hành)
- GitOps: ArgoCD và Argo Rollouts
- Monitoring: Prometheus và Grafana
- Security: Quét lỗ hổng image với Trivy

---

## HƯỚNG DẪN TRIỂN KHAI HẠ TẦNG

1. KHỞI TẠO TÀI NGUYÊN (TERRAFORM)
- Di chuyển vào thư mục terraform: `cd terraform`
- Khởi tạo và thực thi:
  ```bash
  terraform init
  terraform apply -auto-approve
  ```
- Kết quả: Terraform sẽ tạo các EC2 Instances trên AWS và tự động tạo file inventory tại `inventory/mycluster/hosts.yaml`.

2. CÀI ĐẶT CỤM KUBERNETES (KUBESPRAY)
- Đảm bảo đã cài đặt Ansible và clone Kubespray.
- Chạy playbook để cài đặt cụm:
  ```bash
  ansible-playbook -i inventory/mycluster/hosts.yaml --become --become-user=root cluster.yml
  ```

3. CÀI ĐẶT ARGOCD
- Tạo namespace và cài đặt:
  ```bash
  kubectl create namespace argocd
  kubectl apply -n argocd -f https://raw.githubusercontent.com/argoproj/argo-cd/stable/manifests/install.yaml
  ```
- Truy cập Dashboard:
  ```bash
  kubectl patch svc argocd-server -n argocd -p '{"spec": {"type": "NodePort"}}'
  ```

4. CÀI ĐẶT MONITORING (PROMETHEUS & GRAFANA)
- Sử dụng Helm để cài đặt Kube-Prometheus-Stack:
  ```bash
  helm repo add prometheus-community https://prometheus-community.github.io/helm-charts
  helm repo update
  helm install monitoring prometheus-community/kube-prometheus-stack --namespace monitoring --create-namespace
  ```

---

## QUY TRÌNH CI/CD

1. CODE INTEGRATION
- Tự động kiểm tra và build mã nguồn khi có thay đổi trên nhánh main.
- Thực hiện quét bảo mật mã nguồn và Docker image bằng Trivy để phát hiện lỗ hổng.

2. ARTIFACT MANAGEMENT
- Đóng gói ứng dụng thành Docker images.
- Tự động gán tag và đẩy (push) image lên Docker Hub.

3. GITOPS WORKFLOW
- Pipeline tự động cập nhật Manifest YAML (Image Tag) trong repository cấu hình.
- ArgoCD theo dõi repository và tự động đồng bộ (sync) trạng thái mong muốn lên Kubernetes cluster.

4. BLUE-GREEN DEPLOYMENT
- Sử dụng Argo Rollouts để thực hiện triển khai Blue-Green.
- Chuyển đổi traffic giữa Active Service và Preview Service để đảm bảo Zero Downtime và có khả năng Rollback tức thì.

---

## HƯỚNG DẪN CHẠY LOCAL (THỬ NGHIỆM)

1. Clone repository:
   git clone https://github.com/davidmoi2135/End-to-End-Monolithic-Deployment.git

2. Khởi chạy bằng Docker Compose:
   docker-compose up --build

3. Truy cập ứng dụng:
   - Frontend: http://localhost:3000
   - Backend API: http://localhost:8081
