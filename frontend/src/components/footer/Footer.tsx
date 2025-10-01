import React from 'react';
import './Footer.css';

function Footer() {
  const year = new Date().getFullYear();
  return (
    <div className='footer'>
      {/* <div>Gamely ©{year}</div> */}
      <div className='rgb-bar' aria-hidden="true"></div>
    </div>
  );
}

export default Footer;
