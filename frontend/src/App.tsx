import './App.css'
import Footer from './components/footer/Footer'
import Navbar from './components/navbar/Navbar'

function App() {

  return (
    <div className='app'>
      <div className='navbar'>
        <Navbar />
        
      </div>
      <div className='footer'>
        <Footer />
      </div>
    </div>
  )
}

export default App
