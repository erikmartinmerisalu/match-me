function NotFound() {
  return (
    <div style={{
      display: "flex",
      flexDirection: "column",  
      justifyContent: "center", 
      alignItems: "center",     
      height: "100vh"           
    }}>

      <img src="/notFound.png" alt="" style={{width: "200px"}} />

      <div style={{fontSize: "34px"}}>Page Not Found</div>
    </div>
  )
}

export default NotFound