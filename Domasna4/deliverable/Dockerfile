FROM node:14.17.0-alpine as build-step
RUN mkdir -p /app
WORKDIR /app
COPY package.json /app
RUN npm install
COPY . /app
RUN npm run build --prod

FROM nginx:stable-alpine
COPY --from=build-step /app/dist/deliverable /app
COPY nginx.conf /etc/nginx/conf.d/default.conf
EXPOSE 4200:80