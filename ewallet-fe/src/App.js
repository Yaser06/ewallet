import './App.css';
import {BrowserRouter, Routes, Route} from "react-router-dom";
import Home from './components/Home/Home';

function App() {
  return (
    <div className="App">
      <BrowserRouter>
        <Routes>
          <Route exact path="/" Component={Home}></Route>
        </Routes>
      </BrowserRouter>
    </div>
  );
}

export default App;