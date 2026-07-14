output "server_public_ip" {
  description = "The public IP of the DevOps Server"
  value       = aws_instance.devops_server.public_ip
}

output "ssh_command" {
  description = "Command to SSH into the server"
  value       = "ssh -i ../ansible/devops-key.pem ubuntu@${aws_instance.devops_server.public_ip}"
}
