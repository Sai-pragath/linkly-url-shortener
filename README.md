# Linkly - Premium URL Shortener & DevOps Pipeline 🚀

Linkly is a modern, high-performance URL shortener built with **Java Spring Boot**, featuring a beautiful landing page interface, custom link aliases, real-time click tracking analytics, and automatic QR Code generation. 

Beyond just the application, this repository contains a complete, production-ready **DevOps toolchain** to automatically provision infrastructure and deploy the application to AWS.

---

## 🌟 Application Features

- **Blazing Fast Redirects**: Powered by Spring Boot and MySQL.
- **Custom Aliases**: Create branded, memorable short links (e.g., `linkly/my-brand`).
- **QR Code Generation**: Instantly generates scannable QR codes for every shortened URL.
- **Click Analytics**: Tracks total clicks in real-time, displayed on a beautiful dashboard.
- **Modern UI**: A responsive, premium landing-page design built with HTML/CSS and Vanilla JS.

---

## 🏗️ Architecture & DevOps Stack

This project goes beyond code by fully automating the deployment process using industry-standard DevOps tools:

- **Docker**: Multi-stage containerization of the Spring Boot app.
- **Terraform**: Infrastructure-as-Code (IaC) to automatically provision AWS EC2 instances and Security Groups.
- **Ansible**: Configuration management to install Docker, Jenkins, Kubernetes, and Monitoring tools on the server.
- **Kubernetes (k3s)**: Orchestrates the MySQL database (with Persistent Volumes) and the Spring Boot application.
- **Jenkins**: CI/CD pipeline (`Jenkinsfile`) that builds the code, pushes to Docker Hub, and deploys to Kubernetes.
- **Prometheus & Grafana**: Full observability stack deployed via Helm to scrape and visualize Spring Boot Actuator metrics.

---

## 💻 Local Development Setup

If you want to run this application locally on your machine:

### Prerequisites
- Java 21+
- Maven
- MySQL Database

### Steps
1. **Configure Database**: Create a MySQL database named `url_shortener`.
   ```sql
   CREATE DATABASE url_shortener;
   ```
2. **Update Credentials**: Edit `src/main/resources/application.properties` with your local MySQL username and password.
3. **Run the App**: 
   ```bash
   mvn clean spring-boot:run
   ```
4. **Access the UI**: Open your browser and navigate to `http://localhost:8080/`.

---

## ☁️ Cloud Deployment (AWS)

For detailed, step-by-step instructions on how to use Terraform, Ansible, and Jenkins to deploy this application to the cloud, please refer to the dedicated **[DEPLOYMENT.md](DEPLOYMENT.md)** guide included in this repository.

---

## 📜 Project Structure

```
├── src/                  # Spring Boot Java source code
├── devops/               # Infrastructure and Configuration
│   ├── ansible/          # Ansible playbooks (setup-server.yml)
│   ├── k8s/              # Kubernetes manifests for App and MySQL
│   └── terraform/        # Terraform scripts (main.tf, outputs.tf)
├── Dockerfile            # Multi-stage Docker build instructions
├── Jenkinsfile           # CI/CD Pipeline definition
├── pom.xml               # Maven dependencies
├── DEPLOYMENT.md         # Full deployment guide
└── README.md             # This file
```

---
*Built with ❤️ using Java Spring Boot.*
