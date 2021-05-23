 const toggleSideBar= () => {

    if ($(".sidebar").is(":visible")) {

        $(".sidebar").css("display","none")
        $(".content").css("margin-left","0%")
        
    }else{

        $(".sidebar").css("display","block")
        $(".content").css("margin-left","20%") 

    }

 };

 const search= () => {

     let query=$("#search-input").val();
     
     if(query=="")
     {
         $(".search-result").hide();
         
     }else
     {

       // console.log(query);

        let url=`http://localhost:8080/search/${query}`;

        fetch(url).then((response) => {
            
            return response.json();

        }).then((data) => {
           // console.log(data);

          let text=`<div class="list-group">`;

                

          data.forEach((contact)=> {

            text+=`<a href="/user/${contact.id}/contact" class="list-group-item list-group-item-action">${contact.name}</a>`;
              
          });

          text+=`</div>`;

          $('.search-result').html(text);
          $('.search-result').show();

        });

    
     }
 };


 // payment first request

 const paymentStart=()=>
 {
     console.log("payment start")

     let amount=$("#amount").val();
     console.log(amount)

     if(amount=="" || amount==null)
     {
        //  alert("Amount Required !!")
        swal("failed","Amount Required !!", "error")
         return ;
     }

// Request to server to create order

     $.ajax(

        {
            url:'/user/create_order',
            data:JSON.stringify({amount:amount,info:'order_created'}),
            contentType:'application/json',
            type:'post',
            dataType:'json',
            success:function(response)
            {
                console.log(response)

                if(response.status=="created")
                {
                    var options = {
                        "key": "rzp_test_oedAGEHXzWXpTH", // Enter the Key ID generated from the Dashboard
                        "amount": "response.amount", // Amount is in currency subunits. Default currency is INR. Hence, 50000 refers to 50000 paise
                        "currency": "INR",
                        "name": "SMART CONTACT MANAGER",
                        "description": "Donation",
                        "image": "https://example.com/your_logo",
                        "order_id": response.id, //This is a sample Order ID. Pass the `id` obtained in the response of Step 1
                        "handler": function (response){
                            console.log(response.razorpay_payment_id);
                            console.log(response.razorpay_order_id);
                            console.log(response.razorpay_signature)
                            // alert("congratulation !")

                            // Update Payment On Server
                            updatePaymentOnServer(response.razorpay_payment_id,response.razorpay_order_id,"paid")
                            

    
                            
                        },
                        "prefill": {
                            "name": "",
                            "email": "",
                            "contact": ""
                        },
                        "notes": {
                            "address": "LearnCodeWith Kuldeep"
                        },
                        "theme": {
                            "color": "#3399cc"
                        }
                    };

                    var rzp1 = new Razorpay(options);
                    rzp1.on('payment.failed', function (response){
                    console.log(response.error.code);
                    console.log(response.error.description);
                    console.log(response.error.source);
                    console.log(response.error.step);
                    console.log(response.error.reason);
                    console.log(response.error.metadata.order_id);
                    console.log(response.error.metadata.payment_id);
                    // alert("oops !! Payment Failed")
                    swal("failed","OOPs Payment Failed", "error")
                    
                    });
                
                    rzp1.open();
  
                }
            },
            error:function(error)
            {
                console.log(error)
                alert("Something went wrong")
            }
        }
     )
     
    
 }

 function updatePaymentOnServer(payment_id, order_id, status)
 {
    $.ajax(

        {
            url:'/user/update_order',
            data:JSON.stringify({payment_id:payment_id,order_id:order_id, status:status}),
            contentType:'application/json',
            type:'post',
            dataType:'json',

            success:function(response)
            {
                swal("Success","Congratulation Payment successful", "success")
            },

            error:function(error)
            {
                swal("failed","Payment successful ,but we did not get on server, we contact as soon as possible ", "error")
            }
        });    
 }