# ECOMMERCE MONOLITHIC DEPLOYMENT PIPELINE

He thong thuong mai dien tu Full-stack duoc thiet ke de minh hoa quy trinh trien khai DevSecOps hien dai tren nen tang Kubernetes.

---

## KIEN TRUC HE THONG

[Chen anh so do kien truc pipeline va ha tang tai day]

Du an tap trung vao viec tu dong hoa chu ky song phat trien phan mem, bao gom cac thanh phan:
- Giao dien nguoi dung: React (TypeScript)
- Logic nghiep vu: Spring Boot (Java 21)
- Luu tru du lieu: PostgreSQL
- Ha tang trien khai: Kubernetes Cluster

---

## CONG NGHE SU DUNG

BACKEND
- Java 21 va Spring Boot 4.x
- Spring Security voi co che xac thuc JWT
- Spring Data JPA ket noi PostgreSQL

FRONTEND
- React 18 va TypeScript
- Axios giao tiep API

DEVOPS VA HA TANG
- CI/CD Pipeline: GitHub Actions
- Quan ly container: Docker
- Dieu phoi container: Kubernetes
- GitOps: ArgoCD
- Deployment Strategy: Argo Rollouts (Blue-Green Deployment)
- Security: Quet lo hong image voi Trivy

---

## QUY TRINH TRIEN KHAI (CI/CD)

1. CODE INTEGRATION
- Tu dong kiem tra va build ma nguon khi co thay doi tren nhanh main.
- Thuc hien quet bao mat ma nguon va Docker image bang Trivy de phat hien lo hong.

2. ARTIFACT MANAGEMENT
- Dong goi ung dung thanh Docker images.
- Tu dong gan tag va day (push) image len Docker Hub.

3. GITOPS WORKFLOW
- Pipeline tu dong cap nhat Manifest YAML (Image Tag) trong repository cau hinh.
- ArgoCD theo doi repository va tu dong dong bo (sync) trang thai mong muon len Kubernetes cluster.

4. BLUE-GREEN DEPLOYMENT
- Su dung Argo Rollouts de thuc hien trien khai Blue-Green.
- Tao moi truong Preview de kiem tra phien ban moi truoc khi chuyen traffic.
- Chuyen doi traffic giua Active Service va Preview Service de dam bao Zero Downtime va co kha nang Rollback tuc thi.

---

## HUONG DAN CAI DAT LOCAL

YEU CAU
- Docker
- Docker Compose

KHOI CHAY HE THONG
1. Clone repository:
   git clone https://github.com/davidmoi2135/End-to-End-Monolithic-Deployment.git

2. Khoi chay bang Docker Compose:
   docker-compose up --build

3. Truy cap ung dung:
   - Frontend: http://localhost:3000
   - Backend API: http://localhost:8081
