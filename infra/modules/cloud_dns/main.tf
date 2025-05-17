resource "google_dns_record_set" "lb_record" {
  managed_zone = var.zone
  name         = "${var.lb_sub_domain}."
  type         = "A"
  ttl          = 60

  rrdatas = [var.lb_ip]
}
