/** @type {import('next').NextConfig} */
const nextConfig = {
    async rewrites() {
        return [
            {
                source: '/solve/:solver',
                destination: 'http://localhost:8080/solve/:solver',
            },
        ];
    },
};

module.exports = nextConfig;