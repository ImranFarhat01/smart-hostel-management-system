#!/usr/bin/env python3
"""Generate architecture and flowchart diagrams as PNG images."""

from reportlab.lib.pagesizes import landscape, A4
from reportlab.lib.colors import HexColor
from reportlab.pdfgen import canvas
from reportlab.lib.units import inch
import subprocess, os

PRIMARY = HexColor("#1a365d")
ACCENT = HexColor("#2b6cb0")
LIGHT = HexColor("#ebf4ff")
GREEN = HexColor("#276749")
GREEN_BG = HexColor("#f0fff4")
ORANGE = HexColor("#c05621")
ORANGE_BG = HexColor("#fffaf0")
PURPLE = HexColor("#553c9a")
PURPLE_BG = HexColor("#faf5ff")
GRAY = HexColor("#718096")
WHITE = HexColor("#ffffff")
BORDER = HexColor("#a0aec0")

def draw_rounded_rect(c, x, y, w, h, r, fill=None, stroke=None):
    c.saveState()
    if fill: c.setFillColor(fill)
    if stroke: c.setStrokeColor(stroke)
    p = c.beginPath()
    p.moveTo(x+r, y)
    p.lineTo(x+w-r, y)
    p.arcTo(x+w-r, y, x+w, y+r, 270, 90)
    p.lineTo(x+w, y+h-r)
    p.arcTo(x+w-r, y+h-r, x+w, y+h, 0, 90)
    p.lineTo(x+r, y+h)
    p.arcTo(x, y+h-r, x+r, y+h, 90, 90)
    p.lineTo(x, y+r)
    p.arcTo(x, y, x+r, y+r, 180, 90)
    p.close()
    if fill and stroke:
        c.drawPath(p, fill=1, stroke=1)
    elif fill:
        c.drawPath(p, fill=1, stroke=0)
    else:
        c.drawPath(p, fill=0, stroke=1)
    c.restoreState()

def draw_arrow(c, x1, y1, x2, y2, color=GRAY):
    c.saveState()
    c.setStrokeColor(color)
    c.setLineWidth(2)
    c.line(x1, y1, x2, y2)
    # arrowhead
    import math
    angle = math.atan2(y2-y1, x2-x1)
    alen = 8
    c.line(x2, y2, x2 - alen*math.cos(angle-0.4), y2 - alen*math.sin(angle-0.4))
    c.line(x2, y2, x2 - alen*math.cos(angle+0.4), y2 - alen*math.sin(angle+0.4))
    c.restoreState()

def architecture_diagram():
    w, h = landscape(A4)
    path = "/home/claude/hostel-management-system/docs/architecture.pdf"
    c = canvas.Canvas(path, pagesize=landscape(A4))

    # Title
    c.setFont("Helvetica-Bold", 22)
    c.setFillColor(PRIMARY)
    c.drawCentredString(w/2, h-50, "System Architecture — Smart Hostel Management System")
    c.setFont("Helvetica", 11)
    c.setFillColor(GRAY)
    c.drawCentredString(w/2, h-68, "Layered MVC Architecture with REST API")

    # Layers
    layers = [
        ("CLIENT LAYER", "Postman / Web Frontend / Mobile App", HexColor("#e2e8f0"), HexColor("#4a5568"), h-130),
        ("CONTROLLER LAYER", "StudentController | RoomController | FeeController | ComplaintController", LIGHT, ACCENT, h-210),
        ("SERVICE LAYER", "StudentService | RoomService | FeeService | ComplaintService | AllocationEngine", GREEN_BG, GREEN, h-290),
        ("REPOSITORY LAYER", "StudentRepository | RoomRepository | FeeRepository | ComplaintRepository", PURPLE_BG, PURPLE, h-370),
        ("DATABASE LAYER", "H2 In-Memory (Dev) / MySQL 8.0 (Production)", ORANGE_BG, ORANGE, h-450),
    ]

    box_w = w - 160
    box_h = 55
    box_x = 80

    for title, desc, bg, fg, y in layers:
        draw_rounded_rect(c, box_x, y, box_w, box_h, 8, fill=bg, stroke=fg)
        c.setFont("Helvetica-Bold", 13)
        c.setFillColor(fg)
        c.drawCentredString(w/2, y+box_h-20, title)
        c.setFont("Helvetica", 9)
        c.setFillColor(HexColor("#4a5568"))
        c.drawCentredString(w/2, y+12, desc)

    # Arrows between layers
    for i in range(len(layers)-1):
        y1 = layers[i][4]
        y2 = layers[i+1][4] + box_h
        draw_arrow(c, w/2, y1, w/2, y2+2, ACCENT)

    # Side labels
    c.setFont("Helvetica", 9)
    c.setFillColor(GRAY)
    c.drawString(box_x + box_w + 10, layers[0][4]+25, "HTTP / REST")
    c.drawString(box_x + box_w + 10, layers[1][4]+25, "Method Calls")
    c.drawString(box_x + box_w + 10, layers[2][4]+25, "JPA Queries")
    c.drawString(box_x + box_w + 10, layers[3][4]+25, "SQL / JDBC")

    c.save()
    return path

def flowchart_diagram():
    w, h = landscape(A4)
    path = "/home/claude/hostel-management-system/docs/flowchart.pdf"
    c = canvas.Canvas(path, pagesize=landscape(A4))

    c.setFont("Helvetica-Bold", 20)
    c.setFillColor(PRIMARY)
    c.drawCentredString(w/2, h-45, "Complaint Workflow Flowchart")

    # Boxes
    bw, bh = 130, 40
    states = {
        "OPEN": (120, h-180, HexColor("#c6f6d5"), GREEN),
        "IN_PROGRESS": (350, h-180, LIGHT, ACCENT),
        "RESOLVED": (580, h-120, HexColor("#c6f6d5"), GREEN),
        "REJECTED": (580, h-240, HexColor("#fed7d7"), HexColor("#c53030")),
        "ESCALATED": (350, h-310, HexColor("#fefcbf"), HexColor("#b7791f")),
    }

    for label, (x, y, bg, fg) in states.items():
        draw_rounded_rect(c, x, y, bw, bh, 8, fill=bg, stroke=fg)
        c.setFont("Helvetica-Bold", 11)
        c.setFillColor(fg)
        c.drawCentredString(x+bw/2, y+15, label)

    # Arrows with labels
    c.setFont("Helvetica", 9)
    c.setFillColor(GRAY)
    draw_arrow(c, 250, h-160, 350, h-160, ACCENT)
    c.drawString(265, h-150, "assign()")

    draw_arrow(c, 480, h-155, 580, h-110, GREEN)
    c.drawString(505, h-125, "resolve()")

    draw_arrow(c, 480, h-170, 580, h-225, HexColor("#c53030"))
    c.drawString(505, h-210, "reject()")

    draw_arrow(c, 350+bw/2, h-180-bh+40, 350+bw/2, h-310+bh, HexColor("#b7791f"))
    c.drawString(420, h-268, "escalate()")

    draw_arrow(c, 120+bw/2, h-180-bh+40, 280, h-310+bh, HexColor("#b7791f"))
    c.drawString(160, h-268, "escalate()")

    # Legend
    c.setFont("Helvetica-Bold", 11)
    c.setFillColor(PRIMARY)
    c.drawString(80, h-400, "Legend:")
    c.setFont("Helvetica", 10)
    c.setFillColor(GRAY)
    c.drawString(80, h-418, "Guards: Cannot resolve/reject closed complaints. Cannot move non-OPEN to IN_PROGRESS.")
    c.drawString(80, h-436, "Escalation sets priority to CRITICAL and triggers notification logging.")

    c.save()
    return path

if __name__ == "__main__":
    p1 = architecture_diagram()
    p2 = flowchart_diagram()
    print(f"Architecture: {p1}")
    print(f"Flowchart: {p2}")
