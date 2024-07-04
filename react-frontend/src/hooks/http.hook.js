import { useState, useCallback } from "react";

export const useHttp = () => {
    const [loading, setLoading] = useState(false);
    const [error, setError] = useState(null);

    const request = useCallback(
        async (
            url,
            method = 'GET',
            body = null,
            headers = {'Content-Type': 'application/json'},
            query = null, 
            limit = null,
            offset = null
            ) => {

        setLoading(true);

        try {
            let requestUrl = url;
            if(limit !== null && offset !== null && query !== null) {
                requestUrl += `?query=${query}&limit=${limit}&offset=${offset}`;
            }
            const response = await fetch(requestUrl, {method, body, headers});

            const data = await response.json();

            setLoading(false);
            return data;
        } catch(e) {
            setLoading(false);
            setError(e.message);
            throw e;
        }
    }, []);

    const clearError = useCallback(() => setError(null), []);

    return {loading, request, error, clearError}
}
