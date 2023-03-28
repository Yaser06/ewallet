import React, {useState, useEffect} from "react";
import './Post.css';

function Post(){
    const [error, setError] = useState(null);
    const [isLoaded, setIsLoaded] = useState(false);
    const [postList, setPostList] = useState([]);
    const [transactionTypeOptions, setTransactionTypeOptions] = useState([]);


    const postRequest = async (walletPublicKey, transactionType, amount) => {
        try {
          const response = await fetch("/api/v1/moneybalance/update?walletPublicKey="+walletPublicKey+"&transactionType="+transactionType+"&amount="+amount, {
            method: "PUT",
            headers: {
              Authorization: "Basic dXNlcjpwYXNzd29yZA==",
              "Content-Type": "application/json",
            },
        
          });
          const data = await response.json();
          console.log(data);
          window.location.reload();
          // burada gerekli işlemler yapılabilir
        } catch (error) {
          console.log(error);
          // burada gerekli hata işlemleri yapılabilir
        }
       
      };

  
     
    
      const handlePostClick = (walletPublicKey, transactionType, amount, event) => {
        event.preventDefault();
        postRequest(walletPublicKey, transactionType, amount);
      };
      
      
      useEffect(() => {
        setTransactionTypeOptions(["Withdraw", "AddMoney"]);
      }, []);

    useEffect(() => {
        fetch("/api/v1/wallet/list", {
            headers: {
              Authorization: "Basic dXNlcjpwYXNzd29yZA==",
            },
          })
        .then(res => res.json())
        .then(
            (result) => {
                setIsLoaded(true);
                if (Array.isArray(result.result)) { // API response'da result array'i kullanılıyor
                    setPostList(result.result);
                } else {
                    setError(new Error("API response is not an array"));
                }
            },
            (error) => {
                setIsLoaded(true);
                setError(error);
            }
        )
    }, [])

    if(error) {
        return <div>Error!!!</div>;
    }
    else if(!isLoaded) {
        return <div>Loading !!!</div>
    }
    else {
       

        const renderTableData = () => {
            if (!Array.isArray(postList)) {
              console.warn("postList is not an array:", postList);
              return null;
            }
            return (
                postList &&
                postList.map((item) => {
                  const { id, walletPublicKey, walletType, moneyBalance = {}, createdDate, provider } = item;
                  const { amount = 0, currency = "" } = moneyBalance || {};
                  return (
                    <tr key={id}>
                      <td>{id}</td>
                      <td>{walletType}</td>
                      <td>{walletPublicKey}</td>
                      <td>{amount}</td>
                      <td>{currency}</td>
                      <td>{createdDate}</td>
                      <td>{provider}</td>
                      <td>

                      <select id={`transactionType-${id}`}>
              {transactionTypeOptions.map((option, index) => (
                <option key={index} value={option}>{option}</option>
              ))}
            </select>
                <input id={`amount-${id}`} type="text" placeholder="Amount" />
                <button onClick={(event) => handlePostClick(item.walletPublicKey, document.getElementById(`transactionType-${id}`).value, document.getElementById(`amount-${id}`).value, event)}>Post</button>
              

                        </td>

                    </tr>
                  );
                })
              );
          };
          
          return (
            <table striped bordered hover >
              <thead>
                <tr>
                  <th>ID</th>
                  <th>Wallet Type</th>
                  <th>Wallet Public Key</th>
                  <th>Amount</th>
                  <th>Currency</th>
                  <th>Created Date</th>
                  <th>Provider</th>
                  <th>Action</th>
                </tr>
              </thead>
              <tbody>{renderTableData()}</tbody>
            </table>
          );
    }
}

export default Post;
