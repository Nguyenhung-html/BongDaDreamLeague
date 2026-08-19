const API='http://localhost:8080/api/voucher'
async function read(res){const t=await res.text();let d={};try{d=t?JSON.parse(t):{}}catch{d={message:t}}if(!res.ok)throw new Error(d.message||'Yêu cầu thất bại!');return d}
const token=()=>localStorage.getItem('token')||''
export default {
 async all(){return read(await fetch(`${API}/admin`,{headers:{Authorization:`Bearer ${token()}`}}))},
 async add(p){return read(await fetch(`${API}/admin`,{method:'POST',headers:{'Content-Type':'application/json',Authorization:`Bearer ${token()}`},body:JSON.stringify(p)}))},
 async edit(id,p){return read(await fetch(`${API}/admin/${id}`,{method:'PUT',headers:{'Content-Type':'application/json',Authorization:`Bearer ${token()}`},body:JSON.stringify(p)}))},
 async remove(id){return read(await fetch(`${API}/admin/${id}`,{method:'DELETE',headers:{Authorization:`Bearer ${token()}`}}))},
 async check(ma,donHang){return read(await fetch(`${API}/kiem-tra`,{method:'POST',headers:{'Content-Type':'application/json',Authorization:`Bearer ${token()}`},body:JSON.stringify({ma,donHang})}))}
}
