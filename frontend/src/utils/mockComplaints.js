// Mock complaint data + lifecycle service, backed by localStorage.
// This is the ONLY place that knows about storage — swap the bodies of these
// functions for real `api.js` calls later and keep the same names/return
// shapes so pages never need to change.

const STORAGE_KEY = "complaints";

// ---------------------------------------------------------------------------
// Canonical lifecycle
// ---------------------------------------------------------------------------

export const STATUS = {
  OPEN: "OPEN",
  ASSIGNED: "ASSIGNED",
  IN_PROGRESS: "IN_PROGRESS",
  RESOLVED: "RESOLVED",
  CLOSED: "CLOSED",
  REJECTED: "REJECTED",
  // User said "no, not solved" after RESOLVED — awaiting admin reassignment.
  NEEDS_REASSIGNMENT: "NEEDS_REASSIGNMENT",
};

export const STATUS_LABELS = {
  [STATUS.OPEN]: "Open",
  [STATUS.ASSIGNED]: "Assigned",
  [STATUS.IN_PROGRESS]: "In Progress",
  [STATUS.RESOLVED]: "Resolved",
  [STATUS.CLOSED]: "Closed",
  [STATUS.REJECTED]: "Rejected",
  [STATUS.NEEDS_REASSIGNMENT]: "Needs Reassignment",
};

// Ordered "happy path" used to drive the visual lifecycle stepper.
export const LIFECYCLE_STEPS = [
  STATUS.OPEN,
  STATUS.ASSIGNED,
  STATUS.IN_PROGRESS,
  STATUS.RESOLVED,
  STATUS.CLOSED,
];

export const PRIORITIES = ["Low", "Medium", "High"];
export const CATEGORIES = ["Plumbing", "Electrical", "Civil", "Billing", "Other"];

// Kept here (instead of duplicated per-page) so customer/agent/admin screens
// always show the same set of agents.
export const AVAILABLE_AGENTS = [
  { name: "Neha Singh", department: "Water Supply" },
  { name: "Raj Malhotra", department: "Electricity" },
  { name: "Kavita Rao", department: "Road Maintenance" },
  { name: "Suresh Nair", department: "Garbage Collection" },
];

// ---------------------------------------------------------------------------
// Attachments
// ---------------------------------------------------------------------------

export const ATTACHMENT_TYPE = { IMAGE: "image", VIDEO: "video" };

export const ALLOWED_IMAGE_TYPES = ["image/jpeg", "image/jpg", "image/png", "image/webp"];
export const ALLOWED_VIDEO_TYPES = ["video/mp4", "video/quicktime", "video/webm"];

// Easy to change once the backend defines the real limit — nothing else
// needs to be touched.
export const MAX_ATTACHMENT_SIZE_MB = 25;

// In-memory only (never persisted). Object URLs die on reload anyway, so we
// don't pretend they survive one — this just gives instant previews for the
// current session without stuffing binaries into localStorage.
const previewCache = new Map();

function attachmentTypeFor(mimeType) {
  if (ALLOWED_IMAGE_TYPES.includes(mimeType)) return ATTACHMENT_TYPE.IMAGE;
  if (ALLOWED_VIDEO_TYPES.includes(mimeType)) return ATTACHMENT_TYPE.VIDEO;
  return null;
}

// Frontend validation only — kept in one place so the size/type rules are
// easy to change once real backend limits exist.
export function validateAttachmentFile(file) {
  const type = attachmentTypeFor(file.type);
  if (!type) {
    return { ok: false, reason: `"${file.name}" isn't a supported photo or video format.` };
  }
  const maxBytes = MAX_ATTACHMENT_SIZE_MB * 1024 * 1024;
  if (file.size > maxBytes) {
    return { ok: false, reason: `"${file.name}" is larger than ${MAX_ATTACHMENT_SIZE_MB}MB.` };
  }
  return { ok: true, type };
}

// Converts a browser File into the mock Attachment shape described in the
// spec (metadata only — no binary is ever written to localStorage).
// Returns { attachment, previewUrl } — previewUrl is only good for this tab.
export function createAttachmentFromFile(file) {
  const { ok, reason, type } = validateAttachmentFile(file);
  if (!ok) {
    throw new Error(reason);
  }
  const attachment = {
    id: uid("att"),
    fileName: file.name,
    contentType: file.type,
    fileSize: file.size,
    type,
  };
  const previewUrl = URL.createObjectURL(file);
  previewCache.set(attachment.id, previewUrl);
  return { attachment, previewUrl };
}

// Returns a live preview URL if this attachment was created in this session,
// otherwise null (e.g. after a page reload) so the UI can fall back to a
// file-type icon instead of a broken image.
export function getAttachmentPreview(attachmentId) {
  return previewCache.get(attachmentId) || null;
}

export function releaseAttachmentPreview(attachmentId) {
  const url = previewCache.get(attachmentId);
  if (url) {
    URL.revokeObjectURL(url);
    previewCache.delete(attachmentId);
  }
}

// ---------------------------------------------------------------------------
// Small helpers
// ---------------------------------------------------------------------------

function uid(prefix = "id") {
  return `${prefix}_${Date.now()}_${Math.random().toString(36).slice(2, 8)}`;
}

function nowIso() {
  return new Date().toISOString();
}

// Appends an auditable, system-authored timeline entry. Every lifecycle
// transition gets one of these so the Activity/Updates timeline can show
// "Status Changed" entries alongside manual Agent/User updates.
function pushSystemEntry(complaint, { type, description, statusFrom, statusTo }) {
  complaint.updates.push({
    id: uid("upd"),
    type, // "created" | "status_change" | "resolution_confirmation" | "closed"
    title: null,
    description: description || null,
    createdBy: "System",
    role: "System",
    createdAt: nowIso(),
    statusFrom: statusFrom || null,
    statusTo: statusTo || null,
  });
}

// ---------------------------------------------------------------------------
// Migration — old localStorage data (or the old seed shape) gets upgraded to
// the new model the first time it's read, so nothing breaks for anyone who
// already has complaints saved from before this change.
// ---------------------------------------------------------------------------

const LEGACY_STATUS_MAP = {
  Pending: STATUS.OPEN,
  "In Progress": STATUS.IN_PROGRESS,
  Resolved: STATUS.RESOLVED,
};

function normalizeStatus(status) {
  if (STATUS[status]) return status;
  return LEGACY_STATUS_MAP[status] || STATUS.OPEN;
}

function migrateComplaint(c) {
  return {
    id: c.id,
    title: c.title,
    category: c.category || "Other",
    priority: c.priority || "Medium",
    description: c.description || "",
    status: normalizeStatus(c.status),
    createdAt: c.createdAt || nowIso(),
    createdBy: c.createdBy || "Customer",
    assignedAgent: c.assignedAgent ?? c.agent ?? null,
    attachments: Array.isArray(c.attachments) ? c.attachments : [],
    updates: Array.isArray(c.updates) ? c.updates : [],
  };
}

const seedData = [
  {
    id: 101,
    title: "Water Leakage in Block B",
    category: "Plumbing",
    priority: "High",
    description: "Continuous water leakage near the stairwell on the 2nd floor.",
    status: STATUS.OPEN,
    createdAt: "2026-07-20T09:00:00.000Z",
    createdBy: "Customer",
    assignedAgent: null,
    attachments: [],
    updates: [
      {
        id: "upd_seed_101",
        type: "created",
        title: null,
        description: "Continuous water leakage near the stairwell on the 2nd floor.",
        createdBy: "Customer",
        role: "User",
        createdAt: "2026-07-20T09:00:00.000Z",
        statusFrom: null,
        statusTo: STATUS.OPEN,
      },
    ],
  },
  {
    id: 102,
    title: "Electricity Fluctuation",
    category: "Electrical",
    priority: "Medium",
    description: "Frequent power fluctuations in the evening.",
    status: STATUS.IN_PROGRESS,
    createdAt: "2026-07-18T10:30:00.000Z",
    createdBy: "Customer",
    assignedAgent: "Neha Singh",
    attachments: [],
    updates: [
      {
        id: "upd_seed_102a",
        type: "created",
        title: null,
        description: "Frequent power fluctuations in the evening.",
        createdBy: "Customer",
        role: "User",
        createdAt: "2026-07-18T10:30:00.000Z",
        statusFrom: null,
        statusTo: STATUS.OPEN,
      },
      {
        id: "upd_seed_102b",
        type: "status_change",
        title: null,
        description: "Assigned to Neha Singh.",
        createdBy: "System",
        role: "System",
        createdAt: "2026-07-18T12:00:00.000Z",
        statusFrom: STATUS.OPEN,
        statusTo: STATUS.ASSIGNED,
      },
      {
        id: "upd_seed_102c",
        type: "status_change",
        title: null,
        description: "Agent started working on this complaint.",
        createdBy: "System",
        role: "System",
        createdAt: "2026-07-18T13:00:00.000Z",
        statusFrom: STATUS.ASSIGNED,
        statusTo: STATUS.IN_PROGRESS,
      },
    ],
  },
  {
    id: 103,
    title: "Road Damage Near Gate 2",
    category: "Civil",
    priority: "Low",
    description: "Large pothole causing trouble for vehicles.",
    status: STATUS.CLOSED,
    createdAt: "2026-07-10T08:15:00.000Z",
    createdBy: "Customer",
    assignedAgent: "Neha Singh",
    attachments: [],
    updates: [
      {
        id: "upd_seed_103a",
        type: "created",
        title: null,
        description: "Large pothole causing trouble for vehicles.",
        createdBy: "Customer",
        role: "User",
        createdAt: "2026-07-10T08:15:00.000Z",
        statusFrom: null,
        statusTo: STATUS.OPEN,
      },
      {
        id: "upd_seed_103b",
        type: "status_change",
        title: null,
        description: "Pothole filled and road resurfaced.",
        createdBy: "System",
        role: "System",
        createdAt: "2026-07-12T09:00:00.000Z",
        statusFrom: STATUS.IN_PROGRESS,
        statusTo: STATUS.RESOLVED,
      },
      {
        id: "upd_seed_103c",
        type: "resolution_confirmation",
        title: null,
        description: "Issue confirmed as solved.",
        createdBy: "Customer",
        role: "User",
        createdAt: "2026-07-12T15:00:00.000Z",
        statusFrom: STATUS.RESOLVED,
        statusTo: STATUS.CLOSED,
      },
    ],
  },
  {
    id: 104,
    title: "Garbage Not Collected for 3 Days",
    category: "Other",
    priority: "Medium",
    description: "Garbage bins near Block C have not been emptied since Monday.",
    status: STATUS.ASSIGNED,
    createdAt: "2026-09-05T09:00:00.000Z",
    createdBy: "Customer",
    assignedAgent: "Neha Singh",
    attachments: [],
    updates: [
      {
        id: "upd_seed_104a",
        type: "created",
        title: null,
        description: "Garbage bins near Block C have not been emptied since Monday.",
        createdBy: "Customer",
        role: "User",
        createdAt: "2026-09-05T09:00:00.000Z",
        statusFrom: null,
        statusTo: STATUS.OPEN,
      },
      {
        id: "upd_seed_104b",
        type: "status_change",
        title: null,
        description: "Assigned to Neha Singh.",
        createdBy: "System",
        role: "System",
        createdAt: "2026-09-05T11:00:00.000Z",
        statusFrom: STATUS.OPEN,
        statusTo: STATUS.ASSIGNED,
      },
    ],
  },
];

function loadComplaints() {
  const raw = localStorage.getItem(STORAGE_KEY);
  if (!raw) {
    saveComplaints(seedData);
    return seedData;
  }
  try {
    const parsed = JSON.parse(raw);
    const migrated = parsed.map(migrateComplaint);
    saveComplaints(migrated);
    return migrated;
  } catch {
    saveComplaints(seedData);
    return seedData;
  }
}

function saveComplaints(list) {
  localStorage.setItem(STORAGE_KEY, JSON.stringify(list));
}

// ---------------------------------------------------------------------------
// Reads
// ---------------------------------------------------------------------------

// TODO: replace with `return api.get("/complaints")` once the backend exists.
export function getComplaints() {
  return loadComplaints();
}

// TODO: replace with `return api.get(`/complaints/${id}`)` once the backend exists.
export function getComplaintById(id) {
  const list = loadComplaints();
  return list.find((c) => String(c.id) === String(id)) || null;
}

// TODO: replace with `return api.get(`/agents/${agentName}/complaints`)`.
export function getComplaintsByAgent(agentName) {
  return loadComplaints().filter((c) => c.assignedAgent === agentName);
}

// ---------------------------------------------------------------------------
// Writes / lifecycle transitions
// ---------------------------------------------------------------------------

// TODO: replace with `return api.post("/complaints", data)` once the backend exists.
export function addComplaint({ title, category, priority, description, createdBy, attachments }) {
  const list = loadComplaints();
  const newComplaint = {
    id: Date.now(),
    title,
    category,
    priority: priority || "Medium",
    description,
    status: STATUS.OPEN,
    createdAt: nowIso(),
    createdBy: createdBy || "Customer",
    assignedAgent: null,
    attachments: attachments || [],
    updates: [],
  };
  pushSystemEntry(newComplaint, {
    type: "created",
    description,
    statusFrom: null,
    statusTo: STATUS.OPEN,
  });
  const updated = [newComplaint, ...list];
  saveComplaints(updated);
  return newComplaint;
}

// Admin assigns (or reassigns, e.g. after NEEDS_REASSIGNMENT) an agent.
// TODO: replace with `return api.post(`/complaints/${id}/assign`, { agentName })`.
export function assignAgent(id, agentName) {
  const list = loadComplaints();
  const updated = list.map((c) => {
    if (String(c.id) !== String(id)) return c;
    const from = c.status;
    const next = { ...c, assignedAgent: agentName, status: STATUS.ASSIGNED };
    pushSystemEntry(next, {
      type: "status_change",
      description: `Assigned to ${agentName}.`,
      statusFrom: from,
      statusTo: STATUS.ASSIGNED,
    });
    return next;
  });
  saveComplaints(updated);
  return getComplaintById(id);
}

// Admin/Agent action for the OPEN → REJECTED branch.
// TODO: replace with `return api.post(`/complaints/${id}/reject`, { reason })`.
export function rejectComplaint(id, { reason, author = "Admin" } = {}) {
  const list = loadComplaints();
  const updated = list.map((c) => {
    if (String(c.id) !== String(id)) return c;
    const from = c.status;
    const next = { ...c, status: STATUS.REJECTED };
    pushSystemEntry(next, {
      type: "status_change",
      description: reason || `Complaint rejected by ${author}.`,
      statusFrom: from,
      statusTo: STATUS.REJECTED,
    });
    return next;
  });
  saveComplaints(updated);
  return getComplaintById(id);
}

// Adds a manual User/Agent update. Enforces "not completely empty".
// Also drives the one bit of "automatic" progression the spec calls for:
// an Agent posting an update on an ASSIGNED complaint moves it to
// IN_PROGRESS, since that's the natural signal that work has started.
// TODO: replace with `return api.post(`/complaints/${id}/updates`, data)`.
export function addUpdate(id, { title, description, author, role }) {
  const cleanTitle = (title || "").trim();
  const cleanDescription = (description || "").trim();
  if (!cleanTitle && !cleanDescription) {
    throw new Error("Please provide an update title or description.");
  }
  const list = loadComplaints();
  const updated = list.map((c) => {
    if (String(c.id) !== String(id)) return c;
    const next = { ...c, updates: [...c.updates] };
    next.updates.push({
      id: uid("upd"),
      type: "update",
      title: cleanTitle || null,
      description: cleanDescription || null,
      createdBy: author,
      role, // "User" | "Agent" | "Admin"
      createdAt: nowIso(),
      statusFrom: null,
      statusTo: null,
    });
    if (role === "Agent" && next.status === STATUS.ASSIGNED) {
      const from = next.status;
      next.status = STATUS.IN_PROGRESS;
      pushSystemEntry(next, {
        type: "status_change",
        description: "Agent started working on this complaint.",
        statusFrom: from,
        statusTo: STATUS.IN_PROGRESS,
      });
    }
    return next;
  });
  saveComplaints(updated);
  return getComplaintById(id);
}

// Agent marks the complaint resolved.
// TODO: replace with `return api.post(`/complaints/${id}/resolve`, data)`.
export function resolveComplaint(id, { description, author } = {}) {
  const list = loadComplaints();
  const updated = list.map((c) => {
    if (String(c.id) !== String(id)) return c;
    const from = c.status;
    const next = { ...c, status: STATUS.RESOLVED, updates: [...c.updates] };
    if (description && description.trim()) {
      next.updates.push({
        id: uid("upd"),
        type: "update",
        title: null,
        description: description.trim(),
        createdBy: author || next.assignedAgent || "Agent",
        role: "Agent",
        createdAt: nowIso(),
        statusFrom: null,
        statusTo: null,
      });
    }
    pushSystemEntry(next, {
      type: "status_change",
      description: "Marked as resolved — awaiting confirmation from the customer.",
      statusFrom: from,
      statusTo: STATUS.RESOLVED,
    });
    return next;
  });
  saveComplaints(updated);
  return getComplaintById(id);
}

// The customer's Yes/No confirmation step. YES -> CLOSED. NO -> requires
// further handling (NEEDS_REASSIGNMENT), never a generic "reopen".
// TODO: replace with `return api.post(`/complaints/${id}/confirm`, { solved })`.
export function confirmResolution(id, solved) {
  const list = loadComplaints();
  const updated = list.map((c) => {
    if (String(c.id) !== String(id)) return c;
    const next = { ...c, updates: [...c.updates] };
    if (solved) {
      pushSystemEntry(next, {
        type: "resolution_confirmation",
        description: "Issue confirmed as solved.",
        statusFrom: STATUS.RESOLVED,
        statusTo: STATUS.CLOSED,
      });
      next.status = STATUS.CLOSED;
      pushSystemEntry(next, {
        type: "closed",
        description: "Complaint closed.",
        statusFrom: STATUS.CLOSED,
        statusTo: STATUS.CLOSED,
      });
    } else {
      pushSystemEntry(next, {
        type: "resolution_confirmation",
        description: "Customer reported the issue is not solved. Requires reassignment.",
        statusFrom: STATUS.RESOLVED,
        statusTo: STATUS.NEEDS_REASSIGNMENT,
      });
      next.status = STATUS.NEEDS_REASSIGNMENT;
    }
    return next;
  });
  saveComplaints(updated);
  return getComplaintById(id);
}

// Admin-only, per the priority business rule (User sets it, Admin can change
// it, Agent cannot).
// TODO: replace with `return api.patch(`/complaints/${id}`, { priority })`.
export function updatePriority(id, priority) {
  const list = loadComplaints();
  const updated = list.map((c) => (String(c.id) === String(id) ? { ...c, priority } : c));
  saveComplaints(updated);
  return getComplaintById(id);
}