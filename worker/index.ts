interface Env {
    BACKEND: {
        fetch: (request: Request) => Promise<Response>;
    };
}

export default {
    /**
     * Cloudflare Worker entry point.
     * Proxies all requests to the containerized backend using the new Containers tech.
     */
    async fetch(request: Request, env: Env, ctx: ExecutionContext): Promise<Response> {
        const url = new URL(request.url);

        // Add custom headers if needed before proxying
        const modifiedRequest = new Request(request, {
            headers: new Headers(request.headers),
        });

        try {
            // Transparently proxying the request to the container instance
            const response = await env.BACKEND.fetch(modifiedRequest);

            // Optional: Post-process response (e.g., adding security headers)
            const newHeaders = new Headers(response.headers);
            newHeaders.set("X-Powered-By", "Cloudflare-Workers-Containers");

            return new Response(response.body, {
                status: response.status,
                statusText: response.statusText,
                headers: newHeaders,
            });
        } catch (error) {
            console.error("[Worker Proxy Error]:", error);
            return new Response("Service Unavailable. The backend container failed to respond.", {
                status: 503
            });
        }
    },
};
