import { useEffect, useState } from 'react';
import axios from 'axios';
import './account-list.css';

interface Transaction {
  transactionTypeName: string;
  transactionAfterBalance: number;
  transactionDate: string;
  transactionTime: string;
}

function AccountList() {
  const [transactions, setTransactions] = useState<Transaction[]>([]);

  useEffect(() => {
    const userId = localStorage.getItem('userId');
    if (userId) {
      axios.post('https://j12e106.p.ssafy.io/bank/list', {
        userId: parseInt(userId),
      })
      .then((res) => {
        const sorted = [...res.data.data].sort((a, b) => {
          const dateTimeA = a.transactionDate + a.transactionTime;
          const dateTimeB = b.transactionDate + b.transactionTime;
          return dateTimeB.localeCompare(dateTimeA);
        });
        setTransactions(sorted);
      })
      
      .catch((err) => {
        console.error('계좌 내역 조회 실패:', err);
      });
    }
  }, []);

  return (
    <div className="account-list-container">
      <div className="account-summary">총 {transactions.length}건</div>

      {transactions.map((item, index) => {
        const isDeposit = item.transactionTypeName === '입금';
        const sign = isDeposit ? '+' : '-';
        const className = isDeposit ? 'plus' : 'minus';
        
        const date = `${parseInt(item.transactionDate.slice(4, 6))}.${parseInt(item.transactionDate.slice(6, 8))}`;
        const time = `${item.transactionTime.slice(0, 2)}:${item.transactionTime.slice(2, 4)}`;

        return (
          <div key={index} className="account-item">
            <div className="item-date">{date} {time}</div>
            <div className="item-desc">{item.transactionTypeName}</div>
            <div className={`item-amount ${className}`}>
              {sign}{Number(1).toLocaleString()}원
            </div>
          </div>
        );
      })}
    </div>
  );
}

export default AccountList;
