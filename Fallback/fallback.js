const listContent = document.getElementById('scopeListShow');
const mainContent = document.getElementById('buttonDiv');
const scopesArray = []
var redirect_uri = 'http://127.0.0.1:7277/Fallback/fallback.html';
document.getElementById('setClient_id').value = '';
addList()

document.getElementById('redirectForm').addEventListener('submit',function(event) {
    event.preventDefault();
    var client_id = document.getElementById('setClient_id').value;
    var scopeFull = '';
    if (scopesArray.length === 0) {
        alert("No Scopes added");
        return;
    }    
    for (let i = 0; i < scopesArray.length; i++) {
        if (i === 0) {
        scopeFull += scopesArray[i];    
        continue;
        }
        scopeFull += " " +scopesArray[i];
    }
    console.log(`DEBUG ${scopesArray.length} ` + (scopesArray.length === 1 ? `Scope: ${scopeFull}` :`Scopes: ${scopeFull}`));
    var url = 'https://accounts.spotify.com/authorize?' +
        new URLSearchParams({
            response_type: 'code',
            client_id: client_id,
            scope: scopeFull,
            redirect_uri: redirect_uri
        })
    window.location.href = url;
});

function addList(){
    const addListBtn = document.createElement('button');
        addListBtn.href = "#";
        addListBtn.className = "buttonClassWider";
        addListBtn.id = "submitBtn";
        addListBtn.textContent = "Add Scope";
    addListBtn.addEventListener('click', function(event){
        event.preventDefault();
        var setScope = document.getElementById('setScopes').value;
        if (typeof setScope === "string" && setScope.trim().length === 0) {
            return;
        }
        scopesArray.push(setScope);
        loop();
        document.getElementById('setScopes').value = '';
    });
    mainContent.appendChild(addListBtn)
}

function loop(){
    listContent.innerHTML = '';
    for (let i = 0; i < scopesArray.length; i++) {
        const listView = document.createElement('li');
        listView.className = "jsList";
        listView.textContent = " " + scopesArray[i];
        listContent.appendChild(listView);
    }
}