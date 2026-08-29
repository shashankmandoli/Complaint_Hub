// Temporary mock data layer so the customer pages work without a backend.
// Swap the functions below for real calls in services/api.js later —
// keep the same names/return shapes and the pages won't need to change.

const STORAGE_KEY = "complaints";

const seedData = [
  {
    id: 101,
    title: "Water Leakage in Block B",
    category: "Plumbing",
    description: "Continuous water leakage near the stairwell on the 2nd floor.",
    status: "Pending",
    agent: null,
    createdAt: "2026-07-20",
  },
  {
    id: 102,
    title: "Electricity Fluctuation",
    category: "Electrical",
    description: "Frequent power fluctuations in the evening.",
    status: "In Progress",
    agent: "Rohit Sharma",
    createdAt: "2026-07-18",
  },
  {
    id: 103,
    title: "Road Damage Near Gate 2",
    category: "Civil",
    description: "Large pothole causing trouble for vehicles.",
    status: "Resolved",
    agent: "Priya Verma",
    createdAt: "2026-07-10",
  },
];

function loadComplaints() {
  const raw = localStorage.getItem(STORAGE_KEY);
  if (!raw) {
    localStorage.setItem(STORAGE_KEY, JSON.stringify(seedData));
    return seedData;
  }
  try {
    return JSON.parse(raw);
  } catch {
    return seedData;
  }
}

function saveComplaints(list) {
  localStorage.setItem(STORAGE_KEY, JSON.stringify(list));
}

// TODO: replace with `return api.get("/complaints")` once the backend exists.
export function getComplaints() {
  return loadComplaints();
}

// TODO: replace with `return api.get(`/complaints/${id}`)` once the backend exists.
export function getComplaintById(id) {
  const list = loadComplaints();
  return list.find((c) => String(c.id) === String(id)) || null;
}

// TODO: replace with `return api.post("/complaints", data)` once the backend exists.
export function addComplaint({ title, category, description }) {
  const list = loadComplaints();
  const newComplaint = {
    id: Date.now(),
    title,
    category,
    description,
    status: "Pending",
    agent: null,
    createdAt: new Date().toISOString().slice(0, 10),
  };
  const updated = [newComplaint, ...list];
  saveComplaints(updated);
  return newComplaint;
}