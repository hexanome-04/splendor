# Git Cheat Sheet

Template commands for common situations.

 > This assumes you are already familiar with the basics: ```push, pull, commit, clone, merge```.

## Detached State

 * I’ve switched to an earlier version and now I’m detached, how do I get back to a working state with all the stuff I don’t see any more?  
```git checkout master```  
(or any other branch name)

 * I’m detached, but I don’t want to go back to master. How do I create a new branch from here so I don’t interfere with the work of others?   
```git branch my-new-branch```  
```git checkout my-new-branch```  
(Or as a single command: ```git checkout -b my-new-branch```)  
```git push --set-upstream origin my-new-branch```

## Remote Branches

 * Change to a branch that is on origin but not yet on my local system  
```git status```  
 => lists e.g. remotes/origin/foo  
```git checkout foo``` (=> spare the prefix!)

## Target File Diffs

 * Compare this file with version of file in other branch, diff:  
```git diff mybranch master -- myfile.cs```  
(Note: make sure the local branched are up to date, to see the actual diffs.)

 * Show me last commits that changes specific file  
```git log path/to/my.file```

## Restore a Single File

 * If I am on non-master, overwrite a specific file / dir with version from master.  
```git checkout master -- path/to/file```

## Abandon Merge

 * Abandon unsuccessful merge, discard all unmerged files  
```git reset --hard HEAD```

## Kill Branch

 * Delete a branch that I don’t need any more (local and remote) (do this after merged into master)  
```git branch -d rif-ram-mapping-validator```  
```git push --delete origin rif-ram-mapping-validator```

 * Show all merged branches  
```git branch --merged master```

 * Show all unmerged branches  
```git branch --no-merged```

## Erroneous Commits

 * Omg I have some commits I really want to get rid of:  

 > Disclaimer, if you’ve already pushed and you do this, tell your team mates or they will hate you! 
   - local only:  
	```git reset --hard commithashhereoflaststableversion```
   -i already pushed:  
	```git reset --hard commithashhereoflaststableversion```  
	```git push --force origin restify-study-release```
   - Verify it worked:  
	```git diff restify-user-study..origin/restify-user-study```

## Tags

 * Show me all tagged commits:  
	```git tag```

 * Tag a commit so I find it again later: (tags have no spaces, but can have a message)  
	```git tag myFunnytagNameWithoutSpaces```  
	OR:  
	```git tag -m “My message for my tag” myFunnyTagNameWithoutSpaces theactualcommithash```

 * Push a tag so others can use it, too:  
```	git push origin muFunnyTagNameWithoutSpaces```

## Rebase

 > Rebase is better than merge, because it places copies of the original commits on the target branch, instead of a single merge commit.

	Syntax:
	1) Start with branch that has the commits you want to relocate (copy)  
	```git checkout my-branch-with-unmerged features```
	2) Rebase things to the target branch (afterwards the feature branch commits are aligned to latest commit of target branch (but still on feature branch)  
	```git rebase my-target-branch```
	3) Actually get things to the target branch  
	```git checkout my-target branch```  
	```git rebase my branch-with-unmerged-features```
		
