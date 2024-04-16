FROM node:current-slim

WORKDIR /client

COPY /src /client/src
COPY index.html /client/
COPY package*.json /client/
COPY vite.config.js /client/

RUN npm i && npm cache clean --force \
	&& npm i @esbuild/linux-x64 esbuild-linux-64 \
	&& npm run build

EXPOSE 80

CMD ["npm", "run", "preview"]