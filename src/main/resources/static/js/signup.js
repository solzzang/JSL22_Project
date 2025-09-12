document.addEventListener('DOMContentLoaded', function() {
    
    // 닉네임 중복확인 - 요소가 존재할 때만 실행
    const btnCheckNickname = document.getElementById('btnCheckNickname');
    if (btnCheckNickname) {
        btnCheckNickname.addEventListener('click', function() {
            const nickname = document.getElementById('nickname').value.trim();
            const msgElement = document.getElementById('nickMsg');
            
            if (!nickname) {
                msgElement.textContent = '닉네임을 입력해주세요.';
                msgElement.className = 'hint error';
                return;
            }
            
            fetch(`/member/check-nickname?nickname=${encodeURIComponent(nickname)}`)
                .then(response => response.text())
                .then(result => {
                    if (result === 'exist') {
                        msgElement.textContent = '이미 사용중인 닉네임입니다.';
                        msgElement.className = 'hint error';
                    } else {
                        msgElement.textContent = '사용 가능한 닉네임입니다.';
                        msgElement.className = 'hint success';
                    }
                })
                .catch(error => {
                    msgElement.textContent = '중복확인 중 오류가 발생했습니다.';
                    msgElement.className = 'hint error';
                });
        });
    }
    
    // 인증메일 발송
    const btnSendCode = document.getElementById('btnSendCode');
    if (btnSendCode) {
        btnSendCode.addEventListener('click', function() {
            const email = document.getElementById('email').value.trim();
            
            if (!email) {
                alert('이메일을 입력해주세요.');
                return;
            }
            
            if (!isValidEmail(email)) {
                alert('올바른 이메일 형식이 아닙니다.');
                return;
            }
            
            this.disabled = true;
            this.textContent = '발송 중...';
            
            fetch('/member/send-verify-email', {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/x-www-form-urlencoded',
                },
                body: `email=${encodeURIComponent(email)}`
            })
            .then(response => response.text())
            .then(result => {
                if (result === 'exist') {
                    alert('이미 사용중인 이메일입니다.');
                } else if (result === 'success') {
                    alert('인증메일이 발송되었습니다. 이메일을 확인해주세요.');
                    const verifyField = document.getElementById('verifyField');
                    if (verifyField) verifyField.style.display = 'block';
                } else {
                    alert('메일 발송 중 오류가 발생했습니다.');
                }
            })
            .catch(error => {
                alert('메일 발송 중 오류가 발생했습니다.');
            })
            .finally(() => {
                this.disabled = false;
                this.textContent = '인증메일 발송';
            });
        });
    }
    
    // 인증코드 확인
    const btnVerifyCode = document.getElementById('btnVerifyCode');
    if (btnVerifyCode) {
        btnVerifyCode.addEventListener('click', function() {
            const email = document.getElementById('email').value.trim();
            const verifyCode = document.getElementById('verifyCode').value.trim();
            const msgElement = document.getElementById('verifyMsg');
            
            if (!verifyCode) {
                msgElement.textContent = '인증코드를 입력해주세요.';
                msgElement.className = 'hint error';
                return;
            }
            
            fetch('/member/verify-email-code', {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/x-www-form-urlencoded',
                },
                body: `email=${encodeURIComponent(email)}&verifyCode=${encodeURIComponent(verifyCode)}`
            })
            .then(response => response.text())
            .then(result => {
                if (result === 'success') {
                    msgElement.textContent = '이메일 인증이 완료되었습니다.';
                    msgElement.className = 'hint success';
                    document.getElementById('email').readOnly = true;
                    document.getElementById('btnSendCode').disabled = true;
                    this.disabled = true;
                } else {
                    msgElement.textContent = '인증코드가 올바르지 않거나 만료되었습니다.';
                    msgElement.className = 'hint error';
                }
            })
            .catch(error => {
                msgElement.textContent = '인증 확인 중 오류가 발생했습니다.';
                msgElement.className = 'hint error';
            });
        });
    }
    
    // 비밀번호 확인
    const password2 = document.getElementById('password2');
    if (password2) {
        password2.addEventListener('input', function() {
            const password = document.getElementById('password').value;
            const password2 = this.value;
            const msgElement = document.getElementById('pwMatchMsg');
            
            if (password2 && password !== password2) {
                msgElement.textContent = '비밀번호가 일치하지 않습니다.';
                msgElement.className = 'hint error';
            } else if (password2 && password === password2) {
                msgElement.textContent = '비밀번호가 일치합니다.';
                msgElement.className = 'hint success';
            } else {
                msgElement.textContent = '';
                msgElement.className = 'hint';
            }
        });
    }
    
    // 우편번호 검색
    const btnPostcode = document.getElementById('btnPostcode');
    if (btnPostcode) {
        btnPostcode.addEventListener('click', function() {
            new daum.Postcode({
                oncomplete: function(data) {
                    document.getElementById('postcode').value = data.zonecode;
                    document.getElementById('address1').value = data.roadAddress;
                    document.getElementById('address2').focus();
                }
            }).open();
        });
    }
    
    // 폼 제출 처리
    const signupForm = document.getElementById('signupForm');
    if (signupForm) {
        signupForm.addEventListener('submit', function(e) {
            // 이메일 인증 확인
            const verifyMsg = document.getElementById('verifyMsg');
            if (verifyMsg && !verifyMsg.classList.contains('success')) {
                e.preventDefault();
                alert('이메일 인증을 완료해주세요.');
                return false;
            }
            
            // 비밀번호 확인
            const password = document.getElementById('password').value;
            const password2 = document.getElementById('password2').value;
            if (password !== password2) {
                e.preventDefault();
                alert('비밀번호가 일치하지 않습니다.');
                return false;
            }
            
            // 필수 약관 확인
            const agreeTerms = document.getElementById('agreeTerms');
            const agreePrivacy = document.getElementById('agreePrivacy');
            if (!agreeTerms.checked || !agreePrivacy.checked) {
                e.preventDefault();
                alert('필수 약관에 동의해주세요.');
                return false;
            }
            
            return true;
        });
    }
    
    // 이메일 형식 검증 함수
    function isValidEmail(email) {
        const emailRegex = /^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\.[A-Za-z]{2,}$/;
        return emailRegex.test(email);
    }
    
	// 기존 DOMContentLoaded 이벤트 리스너 안에 추가
	const forgotPasswordLink = document.querySelector('.forgot-password-link');
	if (forgotPasswordLink) {
	    forgotPasswordLink.addEventListener('click', function(e) {
	        e.preventDefault(); // 기본 동작 방지
	        e.stopPropagation(); // 이벤트 버블링 방지
	        window.location.href = '/member/forgot-password';
	    });
	}
	
});