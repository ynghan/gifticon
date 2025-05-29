import { useEffect, useState } from 'react';
import axios from 'axios';
import './balance.css';
import AccountList from '../component/account-list';

function AccountPage() {
  const [balance, setBalance] = useState('');
  const [email, setEmail] = useState('');

  useEffect(() => {
    const userId = localStorage.getItem('userId');
    const emailFromStorage = localStorage.getItem('email');

    setEmail(emailFromStorage || '');

    if (userId) {
      axios.post('https://j12e106.p.ssafy.io/bank/balance', {
        userId: parseInt(userId),
      })
      .then((res) => {
        setBalance(res.data.data.balance);
      })
      .catch((err) => {
        console.error('잔액 조회 실패:', err);
      });
    }
  }, []);

  return (
    <div className="account-container">
      {/* 상단 파란색 영역 */}
      <div className="account-header">
        <div className="balance-amount">{Number(balance).toLocaleString()}원</div>
        <div className="balance-number">{email}</div>
      </div>

      {/* 하단 리스트 컴포넌트 */}
      <AccountList />
    </div>
  );
}

export default AccountPage;
