# URL Shortener - Deployment Guide

This guide provides step-by-step instructions to deploy the URL Shortener application to AWS using a complete DevOps toolchain (Terraform, Ansible, Jenkins, Docker, and Kubernetes).

---

## Prerequisites
Before you begin, ensure you have the following installed on your local machine:
1. **AWS CLI** (configured with `aws configure` using an IAM user that has EC2/VPC privileges).
2. **Terraform** (for infrastructure provisioning).
3. **Ansible** (for configuration management).
4. **Git** (to push your code).
5. A **Docker Hub** account.

---

## Step 1: Push Code to a Git Repository
Jenkins will need to pull your code from a repository to build the pipeline.
1. Initialize a git repository (if you haven't already).
2. Commit all the files (including `Jenkinsfile`, `Dockerfile`, and the `devops/` folder).
3. Push the repository to GitHub, GitLab, or Bitbucket.

---

## Step 2: Configure Jenkinsfile
Open the `Jenkinsfile` located in the root of the project and update the environment variables:
- Change `yourdockerhubusername/url-shortener` to your **actual Docker Hub username**.

---

## Step 3: Provision AWS Infrastructure (Terraform)
We will use Terraform to spin up a `t3.large` EC2 instance and configure Security Groups.

1. Open your terminal and navigate to the Terraform directory:
   ```bash
   cd devops/terraform
   ```
2. Initialize Terraform to download the AWS provider:
   ```bash
   terraform init
   ```
3. Apply the configuration to create the server:
   ```bash
   terraform apply -auto-approve
   ```
4. **Important**: When Terraform finishes, it will print the `server_public_ip`. Copy this IP address!

---

## Step 4: Configure the Server (Ansible)
We will use Ansible to SSH into the newly created EC2 instance and install Docker, Jenkins, Kubernetes (k3s), Prometheus, and Grafana.

1. Navigate to the Ansible directory:
   ```bash
   cd ../ansible
   ```
2. Run the Ansible playbook:
   ```bash
   ansible-playbook -i inventory.ini setup-server.yml
   ```
*(Note: This step may take 3-5 minutes as it installs multiple heavy tools).*

---

## Step 5: Setup Jenkins & Docker Hub Credentials
Now that the server is ready, we need to configure Jenkins.

1. Open your browser and go to `http://<EC2_PUBLIC_IP>:8080`.
2. To get the initial administrator password, SSH into your server (using the command output by Terraform) and run:
   ```bash
   sudo cat /var/lib/jenkins/secrets/initialAdminPassword
   ```
3. Paste the password into the browser, select **"Install suggested plugins"**, and create an Admin user.
4. Go to **Manage Jenkins** -> **Credentials** -> **System** -> **Global credentials (unrestricted)**.
5. Click **Add Credentials**:
   - **Kind**: Username with password
   - **Username**: Your Docker Hub username
   - **Password**: Your Docker Hub password (or Access Token)
   - **ID**: `dockerhub-credentials` (This must match exactly what is in the `Jenkinsfile`)
   - Click **Create**.

---

## Step 6: Run the CI/CD Pipeline
1. On the Jenkins dashboard, click **New Item**.
2. Name it `url-shortener-pipeline`, select **Pipeline**, and click **OK**.
3. Scroll down to the **Pipeline** section.
4. Select **Pipeline script from SCM**.
5. Choose **Git**, enter your repository URL, and specify the branch (e.g., `main` or `master`).
6. Click **Save** and then click **Build Now**!

Watch the pipeline stages complete! Jenkins will:
- Build the Spring Boot JAR
- Build the Docker Image
- Push it to Docker Hub
- Deploy it to your Kubernetes Cluster

---

## Step 7: Access the Application and Monitoring!
Once the Jenkins pipeline successfully finishes, your application is live on the internet!

- **URL Shortener App**: `http://<EC2_PUBLIC_IP>:30080`
- **Grafana Dashboards**: `http://<EC2_PUBLIC_IP>:3000` (Default login: `admin` / `prom-operator`)
- **Prometheus UI**: `http://<EC2_PUBLIC_IP>:9090`

> **Note on Grafana**: Once logged into Grafana, you can add a new Dashboard, select Prometheus as the data source, and use pre-built dashboards for Spring Boot (like Dashboard ID: `4701`) or Kubernetes to visualize the metrics!
