import { mergeApplicationConfig, ApplicationConfig } from '@angular/core';
import { provideServerRendering} from '@angular/ssr';
import { appConfig } from './app.config';
import { provideHttpClient, withInterceptors } from '@angular/common/http';
import { credentialsInterceptor } from './interceptors/credentials';
import { serverRoutes } from './app.routes.server';

const serverConfig: ApplicationConfig = {
  providers: [
    provideServerRendering(),
    provideHttpClient(withInterceptors([credentialsInterceptor]))
  ]
};

export const config = mergeApplicationConfig(appConfig, serverConfig);
