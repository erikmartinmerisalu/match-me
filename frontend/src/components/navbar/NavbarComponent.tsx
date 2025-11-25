import { Link, useNavigate } from 'react-router-dom';
import { useAuth } from '../../context/AuthContext';
import ProfilePicShow from '../profilepic/ProfilePicShow';
import Container from 'react-bootstrap/Container';
import Nav from 'react-bootstrap/Nav';
import Navbar from 'react-bootstrap/Navbar';
import NavDropdown from 'react-bootstrap/NavDropdown';
import { Button } from 'react-bootstrap';

function NavbarComponent() {
  const navigate = useNavigate();
  const { loggedIn, signOut, loggedInUserData } = useAuth();

  const handleLogOut = () => {
    signOut();
    navigate("/home");
  };


  return (
     <Navbar expand="lg" className="bg-body-tertiary">

      <Container>
        <Navbar.Brand as={Link} to=" ">Gamely</Navbar.Brand>
        <Navbar.Toggle aria-controls="basic-navbar-nav" />
        <Navbar.Collapse id="basic-navbar-nav" role="navigation">
          {loggedIn === true ? 
            <Nav className="me-auto">
              <Nav.Link as={Link} to="/userprofile" > Profile </Nav.Link>
              <Nav.Link as={Link} to="/recommendations">Discover</Nav.Link>
              <Nav.Link as={Link} to="/matches">Matches</Nav.Link>
              <Nav.Link as={Link} to="/chat">Chat</Nav.Link>
            </Nav>
          :
            <>
            <Nav className="me-auto">
              <></>
            </Nav>
            </>
          }
          
          <Nav>
            <br />
           { loggedIn === false ?
            <>
            <Button onClick={() => navigate("/login")} variant="outline-dark">Log In</Button>
            <Button onClick={() => navigate("/signup")} variant="outline-dark">Sign Up</Button>
          </> : 
          <>
          {loggedInUserData?.profilePic ? <ProfilePicShow src={loggedInUserData.profilePic} width={30} height={30} /> : " "}
          <NavDropdown title={loggedInUserData?.displayName? loggedInUserData.displayName : ""} id="basic-nav-dropdown">
              <NavDropdown.Item onClick={() => handleLogOut()}>Log Out</NavDropdown.Item>
          </NavDropdown>
          </>}
          </Nav>

        </Navbar.Collapse>
      </Container>
    </Navbar>
  )
}

export default NavbarComponent;