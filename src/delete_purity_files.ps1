Get-ChildItem $Path -Recurse | Where{$_.Name -Match "_purity.java"} | Remove-Item