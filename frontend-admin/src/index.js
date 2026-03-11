import { getAssetFromKV } from "@cloudflare/kv-asset-handler";
import manifestJSON from "__STATIC_CONTENT_MANIFEST";
const manifest = JSON.parse(manifestJSON);

export default {
    async fetch(request, env, ctx) {
        try {
            return await getAssetFromKV(
                {
                    request,
                    waitUntil: ctx.waitUntil.bind(ctx),
                },
                {
                    ASSET_NAMESPACE: env.__STATIC_CONTENT,
                    ASSET_MANIFEST: manifest,
                }
            );
        } catch (e) {
            let pathname = new URL(request.url).pathname;
            return new Response(`"${pathname}" not found`, {
                status: 404,
                statusText: "not found",
            });
        }
    },
};
