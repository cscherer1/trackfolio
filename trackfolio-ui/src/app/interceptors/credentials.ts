import { HttpInterceptorFn } from '@angular/common/http';

export const credentialsInterceptor: HttpInterceptorFn = (req, next) => {
  // Ensure cookies (session) are sent to the Spring Boot backend via the dev proxy
  const withCreds = req.clone({ withCredentials: true });
  return next(withCreds);
};
