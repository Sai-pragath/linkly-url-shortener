provider "aws" {
  region = var.aws_region
}

# Get default VPC and Subnet
data "aws_vpc" "default" {
  default = true
}

data "aws_subnets" "default" {
  filter {
    name   = "vpc-id"
    values = [data.aws_vpc.default.id]
  }
}

# Security Group for the Devops Server
resource "aws_security_group" "devops_sg" {
  name        = "url-shortener-devops-sg"
  description = "Security group for Jenkins, K8s, and Monitoring"
  vpc_id      = data.aws_vpc.default.id

  # SSH
  ingress {
    from_port   = 22
    to_port     = 22
    protocol    = "tcp"
    cidr_blocks = ["0.0.0.0/0"]
  }

  # Jenkins
  ingress {
    from_port   = 8080
    to_port     = 8080
    protocol    = "tcp"
    cidr_blocks = ["0.0.0.0/0"]
  }

  # App (K8s NodePort)
  ingress {
    from_port   = 30080
    to_port     = 30080
    protocol    = "tcp"
    cidr_blocks = ["0.0.0.0/0"]
  }

  # Grafana
  ingress {
    from_port   = 30000
    to_port     = 30000
    protocol    = "tcp"
    cidr_blocks = ["0.0.0.0/0"]
  }

  # Prometheus
  ingress {
    from_port   = 30090
    to_port     = 30090
    protocol    = "tcp"
    cidr_blocks = ["0.0.0.0/0"]
  }

  # Allow all outbound
  egress {
    from_port   = 0
    to_port     = 0
    protocol    = "-1"
    cidr_blocks = ["0.0.0.0/0"]
  }
}

# EC2 Instance
resource "aws_instance" "devops_server" {
  ami           = "ami-0c7217cdde317cfec" # Ubuntu 22.04 LTS in us-east-1 (update if different region)
  instance_type = var.instance_type
  subnet_id     = data.aws_subnets.default.ids[0]
  
  vpc_security_group_ids = [aws_security_group.devops_sg.id]
  
  # We assume you have a key pair, or we just rely on EC2 Instance Connect / Ansible via SSM.
  # For simplicity, we create a new key pair dynamically if needed, or leave it blank and let Ansible use password or local ssh agent.
  # We will generate a local key for Ansible.
  key_name = aws_key_pair.devops_key.key_name

  root_block_device {
    volume_size = 30
    volume_type = "gp3"
  }

  tags = {
    Name = "UrlShortener-DevOps-Server"
  }
}

# Generate an SSH key for Ansible to connect
resource "tls_private_key" "ssh_key" {
  algorithm = "RSA"
  rsa_bits  = 4096
}

resource "aws_key_pair" "devops_key" {
  key_name   = "devops-key"
  public_key = tls_private_key.ssh_key.public_key_openssh
}

# Save private key locally for Ansible
resource "local_file" "private_key" {
  content         = tls_private_key.ssh_key.private_key_pem
  filename        = "${path.module}/../ansible/devops-key.pem"
  file_permission = "0400"
}

# Generate Ansible Inventory dynamically
resource "local_file" "ansible_inventory" {
  content = templatefile("${path.module}/inventory.tmpl", {
    ip = aws_instance.devops_server.public_ip
  })
  filename = "${path.module}/../ansible/inventory.ini"
}
