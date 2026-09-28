const API_BASE_URL = "/api";

/**
 * Sends an HTTP request to the Complaint-Hub backend.
 *
 * The backend uses session-based authentication, so credentials
 * are included with every request.
 */
async function request(path, options = {}) {
    const response = await fetch(
        `${API_BASE_URL}${path}`,
        {
            credentials: "include",
            ...options,
        }
    );

    const contentType =
        response.headers.get("content-type") || "";

    let responseBody = null;

    if (contentType.includes("application/json")) {
        responseBody = await response.json();
    } else {
        responseBody = await response.text();
    }

    if (!response.ok) {
        const message =
            responseBody &&
            typeof responseBody === "object" &&
            responseBody.message
                ? responseBody.message
                : `Request failed with status ${response.status}.`;

        throw new Error(message);
    }

    return responseBody;
}

/**
 * Sends a GET request.
 */
export async function get(path) {
    return request(path, {
        method: "GET",
    });
}

/**
 * Sends a POST request.
 *
 * The body can be:
 * - URLSearchParams
 * - FormData
 * - a string
 *
 * We intentionally do not automatically set Content-Type here.
 * The browser will set the correct Content-Type for FormData
 * and URLSearchParams.
 */
export async function post(path, body) {
    return request(path, {
        method: "POST",
        body,
    });
}

/**
 * Sends a PUT request.
 */
export async function put(path, body) {
    return request(path, {
        method: "PUT",
        body,
    });
}

/**
 * Sends a DELETE request.
 */
export async function remove(path) {
    return request(path, {
        method: "DELETE",
    });
}