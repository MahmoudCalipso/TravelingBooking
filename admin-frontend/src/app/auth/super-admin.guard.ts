import { inject } from '@angular/core';
import { CanActivateFn, Router } from '@angular/router';
import { getAuth, onAuthStateChanged } from '@angular/fire/auth';

export const superAdminGuard: CanActivateFn = () => {
  const router = inject(Router);
  const auth = getAuth();

  return new Promise<boolean>(resolve => {
    onAuthStateChanged(auth, async user => {
      if (!user) {
        await router.navigateByUrl('/super-admin-login');
        resolve(false);
        return;
      }
      // Token presence is checked; backend will enforce SUPER_ADMIN role.
      const token = await user.getIdToken();
      localStorage.setItem('fb_id_token', token);
      resolve(true);
    });
  });
};

