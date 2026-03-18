(function () {
  const reissueUrl = '/user-service/auth/reissue';



  function clearAccessToken() {
    localStorage.removeItem('loginUser');
    localStorage.removeItem('JWT');
  }

  async function reissueAccessToken() {
     console.log('[JPAuth] accessToken 재발급 요청');

     const response = await fetch(reissueUrl, {
       method: 'POST',
       headers: {
         'Accept': 'application/json'
       },
       credentials: 'same-origin'
     });

     const contentType = response.headers.get('content-type') || '';
     const data = contentType.includes('application/json') ? await response.json() : null;

	 if (!response.ok || !data || !data.accessToken) {
	   console.warn('[JPAuth] accessToken 재발급 실패', data);
	   clearAccessToken();
	   throw new Error((data && data.error) ? data.error : 'REISSUE_FAILED');
	 }

	 console.log('[JPAuth] accessToken 재발급 성공');

	 return data.accessToken;
   }

   async function authFetch(url, options = {}) {
     const originalOptions = { ...options };
     const originalHeaders = { ...(options.headers || {}) };

     const attemptRequest = async () => {
       return fetch(url, {
         ...originalOptions,
         headers: originalHeaders,
         credentials: 'include'
       });
     };

     let response = await attemptRequest();

     if (response.status !== 401) {
       return response;
     }

     console.warn('[JPAuth] 401 감지 - 재발급 시도');

     await reissueAccessToken();
     response = await attemptRequest();

     return response;
   }

  async function moveWithAuthCheck(event, url, checkUrl) {
    event.preventDefault();

    const response = await authFetch(checkUrl, {
      method: 'GET',
      headers: {
        'Accept': 'application/json'
      }
    });

    if (!response.ok) {
      throw new Error('AUTH_CHECK_FAILED');
    }

    window.location.href = url;

    return false;
  }

  window.JPAuth = {
        clearAccessToken,
        reissueAccessToken,
        authFetch,
        moveWithAuthCheck
      };
  })();